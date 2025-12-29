package com.example.supercompras.model

import java.text.SimpleDateFormat
import java.util.*

data class ItemCompra(
    val texto: String,
    val foiComprado: Boolean = false,
    val dataHora: String = obterDataFormatada()
)

fun obterDataFormatada(): String {
    val localeBR = Locale.forLanguageTag("pt-BR")
    val sdf = SimpleDateFormat("EEEE (dd/MM/yyyy) 'às' HH:mm", localeBR)
    // CORREÇÃO: Define o fuso horário local do dispositivo
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date()).replaceFirstChar { it.uppercase() }
}