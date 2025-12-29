package com.example.supercompras.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.supercompras.R
import com.example.supercompras.model.ItemCompra
import com.example.supercompras.ui.SuperComprasViewModel
import com.example.supercompras.ui.theme.Coral
import com.example.supercompras.ui.theme.Marinho
import com.example.supercompras.ui.theme.Typography

/* =========================
   TELA PRINCIPAL (MVVM)
   ========================= */
@Composable
fun ListaDeComprasScreen(viewModel: SuperComprasViewModel, modifier: Modifier = Modifier) {
    // Observa o estado do Flow vindo do ViewModel
    val listaDeItens by viewModel.listaDeItens.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // Topo da Tela com Imagem e Input
        item {
            ImagemTopo() // Componente agora integrado
            AdcionarItemComponent { textoDigitado ->
                // CORREÇÃO: Passa apenas String, resolvendo o Argument mismatch
                viewModel.adcionarItem(textoDigitado)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // SEÇÃO: LISTA DE COMPRAS
        val itensParaComprar = listaDeItens.filter { !it.foiComprado }

        item {
            TituloComponent("Lista de Compras")
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (itensParaComprar.isEmpty()) {
            item {
                Text(
                    text = "Sua lista está vazia. Adicione itens a ela para não esquecer nada na próxima compra!",
                    style = Typography.bodyLarge,
                    color = Marinho,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
        } else {
            renderLista(itensParaComprar, viewModel)
        }

        // SEÇÃO: COMPRADO
        val itensComprados = listaDeItens.filter { it.foiComprado }
        if (itensComprados.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                TituloComponent("Comprado")
                Spacer(modifier = Modifier.height(12.dp))
            }
            renderLista(itensComprados, viewModel)
        }
    }
}

/* =========================
   FUNÇÃO AUXILIAR DE RENDERIZAÇÃO
   ========================= */
private fun LazyListScope.renderLista(lista: List<ItemCompra>, viewModel: SuperComprasViewModel) {
    items(lista) { item ->
        ItemDaListaComponent(
            item = item,
            aoMudarStatus = { viewModel.mudarStatus(it) },
            aoRemoverItem = { viewModel.removerItem(it) },
            aoEditarItem = { itemAlvo, novoTexto ->
                viewModel.editarItem(itemAlvo, novoTexto)
            }
        )
    }
}

/* =========================
   COMPONENTES DE UI
   ========================= */

@Composable
fun AdcionarItemComponent(aoSalvarItem: (String) -> Unit) {
    var texto by rememberSaveable { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            placeholder = { Text("O que você precisa comprar?", color = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botão com estilo Coral e arredondado
        Button(
            onClick = {
                if (texto.isNotBlank()) {
                    aoSalvarItem(texto)
                    texto = ""
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Coral),
            modifier = Modifier.fillMaxWidth(0.7f).height(48.dp),
            contentPadding = PaddingValues(16.dp, 12.dp)
        ) {
            Text("Salvar item", color = Color.White, style = Typography.bodyLarge)
        }
    }
}

@Composable
fun ItemDaListaComponent(
    item: ItemCompra,
    aoMudarStatus: (ItemCompra) -> Unit,
    aoRemoverItem: (ItemCompra) -> Unit,
    aoEditarItem: (ItemCompra, String) -> Unit
) {
    var editando by remember { mutableStateOf(false) }
    var textoEditado by remember { mutableStateOf(item.texto) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = item.foiComprado,
                onCheckedChange = { aoMudarStatus(item) },
                colors = CheckboxDefaults.colors(checkedColor = Coral)
            )
            if (editando) {
                OutlinedTextField(
                    value = textoEditado,
                    onValueChange = { textoEditado = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )
                IconButton(onClick = {
                    aoEditarItem(item, textoEditado)
                    editando = false
                }) {
                    Icon(Icons.Default.Done, "Salvar", tint = Color(0xFF4CAF50))
                }
            } else {
                Text(item.texto, Modifier.weight(1f), style = Typography.bodyMedium, color = Marinho)
                IconButton(onClick = { aoRemoverItem(item) }) {
                    Icon(Icons.Default.Delete, "Excluir", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { editando = true }) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        }
        // Alinhamento da data abaixo do checkbox
        Text(
            text = item.dataHora,
            modifier = Modifier.padding(start = 12.dp),
            style = Typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun TituloComponent(texto: String) {
    Text(
        text = texto,
        style = Typography.headlineLarge,
        color = Color(0xFFF55B64),
        modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
    )
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 2.5f)
    Canvas(Modifier.fillMaxWidth().height(4.dp)) {
        drawLine(
            color = Coral,
            pathEffect = pathEffect,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            strokeWidth = 4f
        )
    }
}

@Composable
fun ImagemTopo() {
    Image(
        painter = painterResource(id = R.drawable.img_topo),
        contentDescription = null,
        modifier = Modifier.size(150.dp).padding(top = 16.dp)
    )
}