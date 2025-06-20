/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat.conversation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import com.example.compose.jetchat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConversationUiState(val channelName: String, val channelMembers: Int, initialMessages: List<Message>) {
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages: List<Message> = _messages

    // Estado para el indicador de "usuario escribiendo"
    private val _isUserTyping = mutableStateOf(false)
    val isUserTyping: Boolean get() = _isUserTyping.value

    // Estado para el indicador de "otro usuario escribiendo"
    private val _otherUserTyping = mutableStateOf<String?>(null)
    val otherUserTyping: String? get() = _otherUserTyping.value

    // Job para cancelar la corrutina de escritura
    private var typingJob: Job? = null
    private var otherUserTypingJob: Job? = null

    fun addMessage(msg: Message) {
        _messages.add(0, msg) // Add to the beginning of the list
    }

    /**
     * Inicia el indicador de "usuario escribiendo"
     * @param scope CoroutineScope para lanzar la corrutina
     * @param delayMs Tiempo en milisegundos antes de ocultar el indicador (por defecto 2000ms)
     */
    fun startTyping(scope: CoroutineScope, delayMs: Long = 2000) {
        // Cancelar el job anterior si existe
        typingJob?.cancel()

        // Mostrar el indicador
        _isUserTyping.value = true

        // Lanzar una nueva corrutina para ocultar el indicador después del delay
        typingJob = scope.launch {
            delay(delayMs)
            _isUserTyping.value = false
        }
    }

    /**
     * Detiene el indicador de "usuario escribiendo" inmediatamente
     */
    fun stopTyping() {
        typingJob?.cancel()
        _isUserTyping.value = false
    }

    /**
     * Simula que otro usuario está escribiendo
     * @param scope CoroutineScope para lanzar la corrutina
     * @param userName Nombre del usuario que está escribiendo
     * @param delayMs Tiempo en milisegundos antes de enviar el mensaje (por defecto 3000ms)
     */
    fun simulateOtherUserTyping(scope: CoroutineScope, userName: String, delayMs: Long = 3000) {
        // Cancelar cualquier simulación anterior
        otherUserTypingJob?.cancel()

        // Mostrar el indicador de que otro usuario está escribiendo
        _otherUserTyping.value = userName

        otherUserTypingJob = scope.launch {
            // Simular que el usuario está escribiendo
            delay(delayMs)

            // Ocultar el indicador de escritura
            _otherUserTyping.value = null

            // Agregar un mensaje simulado
            val simulatedMessage = Message(
                author = userName,
                content = "Mensaje simulado de $userName",
                timestamp = "Ahora",
            )
            addMessage(simulatedMessage)
        }
    }

    /**
     * Detiene la simulación de escritura de otro usuario
     */
    fun stopOtherUserTyping() {
        otherUserTypingJob?.cancel()
        _otherUserTyping.value = null
    }
}

@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ali else R.drawable.someone_else,
)
