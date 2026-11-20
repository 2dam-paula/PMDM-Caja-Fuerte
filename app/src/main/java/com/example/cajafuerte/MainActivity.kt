package com.example.cajafuerte

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cajafuerte.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import android.animation.ObjectAnimator
import android.animation.ValueAnimator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var sum: Int = 0
    private var numeroUsuario: Int = 0
    private var animacionMano: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initEvent()
    }

    private fun initEvent() {
        binding.txtResultado.visibility = View.INVISIBLE //Poner invisible el resultado
        binding.imageButtonMano.setOnClickListener {
            val textoNumero = binding.editTextNumero.text.toString()
            val numeroObjetivo = textoNumero.toIntOrNull()
            if (numeroObjetivo == null || numeroObjetivo !in 3..18) {
                Toast.makeText(this, "Introduce un número entre 3 y 18", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            numeroUsuario = numeroObjetivo

            binding.imageButtonMano.isEnabled = false
            binding.editTextNumero.isEnabled = false
            binding.txtResultado.visibility = View.VISIBLE

            game(numeroObjetivo)
        }

    }

    fun game(numeroObjetivo: Int) { // Llama a la funcion
        sheduleRun(numeroObjetivo)
    }

    private fun sheduleRun(numeroObjetivo: Int) {
        val msc = 1000L;

        iniciarAnimacionMano()

        lifecycleScope.launch {
            do {
                throwEngranajeInTime() //Ajusta los 3 engranajes
                viewResult() //Muestra el resultado total

                if(sum != numeroUsuario) delay(msc)
            } while (sum != numeroUsuario)

            detenerAnimacionMano()

            //Cuando sale del bucle, es que ha acertado el numero.
            //Activamos la mano y el cuadro de texto
            binding.imageButtonMano.isEnabled = true
            binding.editTextNumero.isEnabled = true

            mostrarDialogoGanador()
        }
    }

    //Metodo que ajusta los engranajes
    private fun throwEngranajeInTime(){
        val numero = Array(3){ Random.nextInt(1, 7) }

        val imagView: Array<ImageView> = arrayOf(
            binding.imageViewRueda1,
            binding.imageViewRueda2,
            binding.imageViewRueda3
        )

        sum = numero.sum()

        for (i in 0..2) {
            selectView(imagView[i], numero[i])
        }
    }

    private fun selectView(imgV: ImageView, v: Int) {
        when (v) {
            1 -> imgV.setImageResource(R.drawable.rueda1)
            2 -> imgV.setImageResource(R.drawable.rueda2)
            3 -> imgV.setImageResource(R.drawable.rueda3)
            4 -> imgV.setImageResource(R.drawable.rueda4)
            5 -> imgV.setImageResource(R.drawable.rueda5)
            6 -> imgV.setImageResource(R.drawable.rueda6)
        }
    }

    //Muestra la suma de la tirada actual.
    private fun viewResult() {
        binding.txtResultado.text = sum.toString()
    }

    //Muestra un AlertDialog informando de que el programa ha acertado el número.
    private fun mostrarDialogoGanador() {
        AlertDialog.Builder(this)
            .setTitle("¡Acertado!")
            .setMessage("Los dados han sacado el número $numeroUsuario.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun iniciarAnimacionMano() {
        // Mueve la mano 40 píxeles a la izquierda y 40 a la derecha en bucle
        animacionMano = ObjectAnimator.ofFloat(binding.imageButtonMano, "translationX", -40f, 40f).apply {
            duration = 250L // Velocidad de cada vaivén (250 ms)
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun detenerAnimacionMano() {
        animacionMano?.cancel()
        binding.imageButtonMano.translationX = 0f // Devuelve la mano exactamente a su posición central original
    }
}