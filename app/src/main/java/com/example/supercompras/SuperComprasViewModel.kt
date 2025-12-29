package com.example.supercompras.ui

import androidx.lifecycle.ViewModel
import com.example.supercompras.model.ItemCompra
import com.example.supercompras.model.obterDataFormatada
import kotlinx.coroutines.flow.*

class SuperComprasViewModel : ViewModel() {
    private val _listaDeItens = MutableStateFlow<List<ItemCompra>>(emptyList())
    val listaDeItens: StateFlow<List<ItemCompra>> = _listaDeItens.asStateFlow()

    // CORREÇÃO: Recebe String e cria o objeto internamente
    fun adcionarItem(textoDigitado: String) {
        _listaDeItens.update { it + ItemCompra(texto = textoDigitado) }
    }

    fun removerItem(item: ItemCompra) {
        _listaDeItens.update { it - item }
    }

    fun mudarStatus(itemAlvo: ItemCompra) {
        _listaDeItens.update { lista ->
            lista.map { if (it == itemAlvo) it.copy(foiComprado = !it.foiComprado) else it }
        }
    }

    fun editarItem(itemAlvo: ItemCompra, novoTexto: String) {
        _listaDeItens.update { lista ->
            lista.map {
                if (it == itemAlvo) it.copy(texto = novoTexto, dataHora = obterDataFormatada())
                else it
            }
        }
    }
}