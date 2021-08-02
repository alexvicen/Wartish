package com.vicen.webel.components.wartish.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.vicen.webel.components.wartish.databinding.IndexBinding
import com.vicen.webel.components.wartish.entidades.Character
import com.vicen.webel.components.wartish.utils.ObjectBox

class IndexActivity : AppCompatActivity() {
    private lateinit var binding: IndexBinding
    private val characterBox = ObjectBox.instance.boxStore.boxFor(Character::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IndexBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (characterBox.all.isEmpty()) {
            characterBox.put(Character("Vicen", 1))
        }

        initialize()
    }

    private fun initialize() {
        binding.btnCandy.setOnClickListener {
            startActivity(Intent(this, ThreeInRowActivity::class.java))
        }
        binding.btnJuegoPrincipal.setOnClickListener {
            resultMainGame.launch(Intent(this, JuegoPrincipal::class.java))
        }
        binding.btnHerreria.setOnClickListener {
            startActivity(Intent(this, Herreria::class.java))
        }
        binding.btnProcesar.setOnClickListener {
            startActivity(Intent(this, ProcesarMateriales::class.java))
        }
        binding.btnCerrarSesion.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private var resultMainGame =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                //TODO resultado error
                return@registerForActivityResult
            }
            //TODO resultado ok
        }
}