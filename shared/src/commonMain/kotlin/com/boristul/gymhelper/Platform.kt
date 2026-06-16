package com.boristul.gymhelper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform