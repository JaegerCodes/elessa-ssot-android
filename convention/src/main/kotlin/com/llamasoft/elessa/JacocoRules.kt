package com.llamasoft.elessa

data class JacocoRules(
    val branches: Rule,
    val instructions: Rule,
    val ignore: List<String>
)

data class Rule(
    val threshold: Float,
    val modules: List<Module>
)

data class Module(
    val module: String,
    val reason: String,
    val threshold: Float
)
