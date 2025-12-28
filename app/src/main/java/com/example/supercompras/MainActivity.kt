package com.example.supercompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.supercompras.ui.theme.Marinho
import com.example.supercompras.ui.theme.SuperComprasTheme
import com.example.supercompras.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* =========================
   MAIN ACTIVITY
   ========================= */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperComprasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListaDeCompras(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/* =========================
   MODELO DE DADOS
   ========================= */
data class ItemCompra(
    val texto: String,
    val foiComprado: Boolean = false,
    val dataHora: String = obterDataFormatada()
)

/* =========================
   UTILITÁRIOS (DATA E HORA)
   ========================= */
fun obterDataFormatada(): String {
    // Correção do deprecated: Uso do Builder ou LanguageTag
    val localeBR = Locale.forLanguageTag("pt-BR")
    val sdf = SimpleDateFormat("EEEE (dd/MM/yyyy) 'às' HH:mm", localeBR)
    return sdf.format(Date()).replaceFirstChar { it.uppercase() }
}

/* =========================
   TELA PRINCIPAL (CORRIGIDA)
   ========================= */
@Composable
fun ListaDeCompras(modifier: Modifier = Modifier) {
    var listaDeItens by rememberSaveable { mutableStateOf(listOf<ItemCompra>()) }

    // CORREÇÃO: Removido o .verticalScroll() para evitar o Fatal Exception
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Topo da tela (Imagem e Input)
        item {
            ImagemTopo()
            AdcionarItem { novoItem ->
                listaDeItens = listaDeItens + novoItem
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // SEÇÃO: LISTA DE COMPRAS (Não comprados)
        val itensParaComprar = listaDeItens.filter { !it.foiComprado }
        if (itensParaComprar.isNotEmpty()) {
            item { Titulo("Lista de Compras") }
            items(itensParaComprar) { item ->
                ItemDaLista(
                    item = item,
                    aoMudarStatus = { alvo ->
                        listaDeItens = listaDeItens.map {
                            if (it == alvo) it.copy(foiComprado = true) else it
                        }
                    },
                    aoRemoverItem = { alvo -> listaDeItens = listaDeItens - alvo },
                    aoEditarItem = { alvo, novoTexto ->
                        // Atualiza texto e data/hora da edição
                        listaDeItens = listaDeItens.map {
                            if (it == alvo) it.copy(texto = novoTexto, dataHora = obterDataFormatada()) else it
                        }
                    }
                )
            }
        }

        // SEÇÃO: COMPRADO (Itens marcados)
        val itensComprados = listaDeItens.filter { it.foiComprado }
        if (itensComprados.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Titulo("Comprado")
            }
            items(itensComprados) { item ->
                ItemDaLista(
                    item = item,
                    aoMudarStatus = { alvo ->
                        listaDeItens = listaDeItens.map {
                            if (it == alvo) it.copy(foiComprado = false) else it
                        }
                    },
                    aoRemoverItem = { alvo -> listaDeItens = listaDeItens - alvo },
                    aoEditarItem = { alvo, novoTexto ->
                        listaDeItens = listaDeItens.map {
                            if (it == alvo) it.copy(texto = novoTexto, dataHora = obterDataFormatada()) else it
                        }
                    }
                )
            }
        }
    }
}

/* =========================
   COMPONENTES AUXILIARES
   ========================= */

@Composable
fun AdcionarItem(aoSalvarItem: (ItemCompra) -> Unit) {
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
        Button(
            onClick = {
                if (texto.isNotBlank()) {
                    aoSalvarItem(ItemCompra(texto = texto))
                    texto = ""
                }
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
        ) {
            Text("Salvar item", color = Color.White)
        }
    }
}

@Composable
fun ItemDaLista(
    item: ItemCompra,
    aoMudarStatus: (ItemCompra) -> Unit,
    aoRemoverItem: (ItemCompra) -> Unit,
    aoEditarItem: (ItemCompra, String) -> Unit
) {
    var editando by remember { mutableStateOf(false) }
    var textoEditado by remember { mutableStateOf(item.texto) }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = item.foiComprado,
                onCheckedChange = { aoMudarStatus(item) }
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
                Text(
                    text = item.texto,
                    modifier = Modifier.weight(1f),
                    style = Typography.bodyMedium,
                    color = Marinho
                )
                IconButton(onClick = { aoRemoverItem(item) }) {
                    Icon(Icons.Default.Delete, "Excluir", tint = Color.Gray)
                }
                IconButton(onClick = { editando = true }) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color.Gray)
                }
            }
        }
        Text(
            text = item.dataHora,
            modifier = Modifier.padding(start = 48.dp),
            style = Typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun Titulo(texto: String) {
    Text(
        text = texto,
        style = Typography.headlineLarge,
        color = Color(0xFFF55B64),
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun ImagemTopo() {
    Image(
        painter = painterResource(id = R.drawable.img_topo),
        contentDescription = null,
        modifier = Modifier.size(150.dp).padding(top = 16.dp)
    )
}