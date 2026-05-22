package com.example.composeapp.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://btuxqlftsgzqwvbhdspx.supabase.co",
    supabaseKey = "sb_publishable_zenLfEYKSEau_-V2U6wCSA_VZ57jL-Z"
) {
    install(Postgrest)
    install(Auth)
}
