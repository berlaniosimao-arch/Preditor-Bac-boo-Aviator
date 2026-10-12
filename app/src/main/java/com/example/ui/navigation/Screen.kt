package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Landing : Screen("landing", "Início")
    object Login : Screen("login", "Iniciar Sessão")
    object Register : Screen("register", "Criar Conta")
    object ForgotPassword : Screen("forgot_password", "Recuperar Senha")
    object Dashboard : Screen("dashboard", "Painel Principal")
    object BacBo : Screen("bac_bo", "Análise Bac Bo")
    object Aviator : Screen("aviator", "Análise Aviator")
    object Bookmakers : Screen("bookmakers", "Casas de Apostas")
    object History : Screen("history", "Histórico de Análises")
    object Premium : Screen("premium", "Plano Premium")
    object Admin : Screen("admin", "Painel Master Admin")
}
