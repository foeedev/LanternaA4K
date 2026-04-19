package com.example.flashlight

import android.hardware.Camera
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var camera: Camera? = null
    private var isFlashOn = false
    private var hasFlash = false

    private lateinit var btnToggle: Button
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnToggle = findViewById(R.id.btnToggle)
        txtStatus = findViewById(R.id.txtStatus)

        // Verifica se o dispositivo tem flash
        hasFlash = packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_FLASH)

        if (!hasFlash) {
            Toast.makeText(this, "Desculpe, seu dispositivo não possui flash", Toast.LENGTH_LONG).show()
            btnToggle.isEnabled = false
            txtStatus.text = "Flash não disponível"
            return
        }

        btnToggle.setOnClickListener {
            toggleFlash()
        }
    }

    private fun toggleFlash() {
        try {
            if (isFlashOn) {
                turnOffFlash()
            } else {
                turnOnFlash()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao acessar o flash: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun turnOnFlash() {
        try {
            camera = Camera.open()
            val parameters = camera!!.parameters
            val flashModes = parameters.supportedFlashModes
            
            if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera!!.parameters = parameters
                camera!!.startPreview()
                isFlashOn = true
                btnToggle.text = "Desligar Lanterna"
                txtStatus.text = "Lanterna LIGADA"
            } else {
                Toast.makeText(this, "Modo torch não suportado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao ligar flash: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun turnOffFlash() {
        try {
            if (camera != null) {
                val parameters = camera!!.parameters
                if (parameters.flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    camera!!.parameters = parameters
                }
                camera!!.stopPreview()
                camera!!.release()
                camera = null
                isFlashOn = false
                btnToggle.text = "Ligar Lanterna"
                txtStatus.text = "Lanterna DESLIGADA"
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao desligar flash: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        // Desliga a lanterna quando o app é pausado
        if (isFlashOn) {
            turnOffFlash()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Garante que a câmera seja liberada
        if (camera != null) {
            try {
                camera!!.release()
                camera = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
