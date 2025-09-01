package com.example.singuploginfirebase

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class DispensadorMenu : AppCompatActivity() {

    // Firebase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refDispensador: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    // Variables normales
    private var currentAgua: Int = 1
    private var currentComida: Int = 1
    private val incrementoAgua = 1
    private val limiteNegativoAgua = 1

    private var isTimerStarted = false

    // UI Components
    private lateinit var btnCamara: ImageButton
    private lateinit var timeButton: Button
    private lateinit var dateButton: Button
    private lateinit var dispensarAgua: AppCompatButton
    private lateinit var dispensarComida: AppCompatButton
    private lateinit var btnRestar: FloatingActionButton
    private lateinit var btnSumar: FloatingActionButton
    private lateinit var txAgua: TextView
    private lateinit var btn2Restar: FloatingActionButton
    private lateinit var btn2Sumar: FloatingActionButton
    private lateinit var txComida: TextView
    private lateinit var btnReiniciar: Button
    private lateinit var txTime: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnMicrofono: ImageButton
    private lateinit var namePet: TextView

    // Temporizador
    private lateinit var countDownTimer: CountDownTimer
    private var timerRunning: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispensador_menu)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        refDispensador = mDatabase.child("users").child(currentUser.uid).child("Dispensador")

        iniciarComponente()
        iniciarListeners()
        iniciarInterfaz()
        setupFirebaseListener()
        loadPetName()

        val atrasInicio = findViewById<AppCompatButton>(R.id.backInicio)
        atrasInicio.setOnClickListener { navegaciónDispensador() }
    }

    private fun setupFirebaseListener() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentAgua = snapshot.child("agua").getValue(Int::class.java) ?: 1
                    currentComida = snapshot.child("comida").getValue(Int::class.java) ?: 1
                    txTime.text = snapshot.child("tiempo").getValue(String::class.java) ?: "00:00:00"
                    dateButton.text = snapshot.child("fecha").getValue(String::class.java) ?: "Fecha"
                    timeButton.text = snapshot.child("hora").getValue(String::class.java) ?: "Hora"

                    setAgua()
                    setComida()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DispensadorMenu, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        refDispensador.addValueEventListener(valueEventListener)
    }

    private fun iniciarComponente() {
        btnRestar = findViewById(R.id.btnRestar)
        btnSumar = findViewById(R.id.btnSumar)
        txAgua = findViewById(R.id.txAgua)
        btn2Restar = findViewById(R.id.btn2Restar)
        btn2Sumar = findViewById(R.id.btn2Sumar)
        txComida = findViewById(R.id.txComida)
        btnCamara = findViewById(R.id.btnCamara)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        txTime = findViewById(R.id.txTime)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnMicrofono = findViewById(R.id.btnMicrofono)
        namePet = findViewById(R.id.namePet)
        timeButton = findViewById(R.id.timeButton)
        dateButton = findViewById(R.id.dateButton)
        dispensarAgua = findViewById(R.id.dispensarAgua)
        dispensarComida = findViewById(R.id.dispensarComida)
    }

    private fun iniciarListeners() {
        dispensarComida.setOnClickListener {
            refDispensador.updateChildren(mapOf(
                "comida" to currentComida,
                "BOTON_COM" to true
            )).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Comida dispensada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al dispensar comida", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dispensarAgua.setOnClickListener {
            refDispensador.updateChildren(mapOf(
                "agua" to currentAgua,
                "BOTON_BEB" to true
            )).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Agua dispensada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al dispensar agua", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Modificar solo el UI al incrementar/disminuir
        btnSumar.setOnClickListener {
            currentAgua += incrementoAgua
            setAgua()
        }

        btnRestar.setOnClickListener {
            currentAgua = maxOf(currentAgua - incrementoAgua, limiteNegativoAgua)
            setAgua()
        }

        btn2Sumar.setOnClickListener {
            currentComida++
            setComida()
        }

        btn2Restar.setOnClickListener {
            currentComida = maxOf(currentComida - 1, 1)
            setComida()
        }

        timeButton.setOnClickListener {
            showTimePickerDialogHour()
        }

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        btnReiniciar.setOnClickListener {
            if (timerRunning) {
                Toast.makeText(this, "Detenga antes el temporizador antes de reiniciar los campos", Toast.LENGTH_SHORT).show()
            } else {
                resetFields()
            }
        }

        btnStart.setOnClickListener {
            if (dateButton.text == "Seleccionar fecha" || timeButton.text == "Hora") {
                Toast.makeText(this, "Por favor selecciona fecha y hora", Toast.LENGTH_SHORT).show()
            } else {
                startTimer()
                // Actualizar Firebase cuando se inicie el temporizador
                updateDispensadorData()
            }
        }

        btnStop.setOnClickListener {
            stopTimer()
        }

        btnMicrofono.setOnClickListener {
            // Implementar funcionalidad del micrófono si es necesario
        }
    }

    private fun updateDispensadorData() {
        val dispensadorData = mapOf(
            "agua" to currentAgua,
            "comida" to currentComida,
            "tiempo" to txTime.text.toString(),
            "fecha" to dateButton.text.toString(),
            "hora" to timeButton.text.toString()
        )

        refDispensador.updateChildren(dispensadorData)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPetName() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = mDatabase.child("users").child(userId)

            userRef.child("nombreMascota").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombreMascota = snapshot.getValue(String::class.java) ?: "Firulais"
                    namePet.text = nombreMascota
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@DispensadorMenu,
                        "Error al cargar el nombre de la mascota: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAgua() {
        txAgua.text = "$currentAgua (S)"
    }

    private fun setComida() {
        txComida.text = "$currentComida (S)"
    }

    private fun iniciarInterfaz() {
        setAgua()
        setComida()
    }

    private fun resetFields() {
        currentAgua = 0
        currentComida = 0
        dateButton.text = "Seleccionar fecha"
        txTime.text = "00:00:00"
        timeButton.text = "Hora"
        dateButton.text = "Fecha"

        val resetData = mapOf(
            "agua" to 0,
            "comida" to 0,
            "tiempo" to "00:00:00",
            "fecha" to "Fecha",
            "hora" to "Hora",
            "BOTON_COM" to false,
            "BOTON_BEB" to false
        )

        refDispensador.updateChildren(resetData)
            .addOnSuccessListener {
                Toast.makeText(this, "Todos los campos han sido reiniciados", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al reiniciar campos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showTimePickerDialogHour() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                timeButton.text = selectedTime
                refDispensador.child("hora").setValue(selectedTime)
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                dateButton.text = selectedDate
                refDispensador.child("fecha").setValue(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun startTimer() {
        // Obtener la fecha y hora seleccionadas
        val dateInfo = dateButton.text.toString().split("/")
        val timeInfo = timeButton.text.toString().split(":")

        // Validar si se ha seleccionado la fecha y la hora
        if (dateButton.text == "Seleccionar fecha" || timeButton.text == "Hora") {
            Toast.makeText(this, "Por favor, seleccione una fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isTimerStarted) {
            // Crear un calendario con la fecha y hora seleccionadas
            val calendar = Calendar.getInstance()
            calendar.set(
                dateInfo[2].toInt(),  // Año
                dateInfo[1].toInt() - 1,  // Mes (los meses empiezan desde 0)
                dateInfo[0].toInt(),  // Día
                timeInfo[0].toInt(),  // Hora
                timeInfo[1].toInt(),  // Minuto
                0  // Segundos
            )

            // Obtener el tiempo actual en milisegundos
            val currentTime = System.currentTimeMillis()
            // Obtener el tiempo seleccionado en milisegundos
            val targetTime = calendar.timeInMillis

            // Calcular la diferencia en tiempo
            val timeLeft = targetTime - currentTime

            if (timeLeft > 0) {
                // Enviar datos a Firebase antes de comenzar el temporizador
                val timerData = mapOf(
                    "ENVIAR_BUTTON" to true,
                    "fecha" to dateButton.text.toString(),
                    "hora" to timeButton.text.toString(),
                    "agua" to currentAgua,
                    "comida" to currentComida
                )

                refDispensador.updateChildren(timerData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Timer iniciado y datos enviados", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al enviar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                // Iniciar el temporizador
                countDownTimer = object : CountDownTimer(timeLeft, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        updateCountDownText(millisUntilFinished)
                    }

                    override fun onFinish() {
                        txTime.text = "00:00:00"
                        refDispensador.child("tiempo").setValue("00:00:00")
                        refDispensador.child("ENVIAR_BUTTON").setValue(false)
                        isTimerStarted = false
                    }
                }
                countDownTimer.start()
                timerRunning = true
                isTimerStarted = true
            } else {
                // Si la fecha seleccionada ya ha pasado
                txTime.text = "00:00:00"
                refDispensador.child("tiempo").setValue("00:00:00")
                Toast.makeText(this, "La fecha seleccionada ya ha pasado", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Si el temporizador ya está corriendo, detenerlo
            if (timerRunning) {
                countDownTimer.cancel()
                timerRunning = false
            }
            txTime.text = "00:00:00"
            refDispensador.child("ENVIAR_BUTTON").setValue(false)
            refDispensador.child("tiempo").setValue("00:00:00")
            Toast.makeText(this, "Temporizador detenido", Toast.LENGTH_SHORT).show()
            isTimerStarted = false
        }
    }


    private fun stopTimer() {
        if (timerRunning) {
            countDownTimer.cancel()
            timerRunning = false
            txTime.text = "00:00:00"
            refDispensador.child("ENVIAR_BUTTON").setValue(false)
            refDispensador.child("tiempo").setValue("00:00:00")
            Toast.makeText(this, "Temporizador detenido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCountDownText(millisUntilFinished: Long) {
        val days = millisUntilFinished / (1000 * 60 * 60 * 24)
        val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
        val minutes = (millisUntilFinished / (1000 * 60)) % 60
        val seconds = (millisUntilFinished / 1000) % 60

        val timeLeftFormatted = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
        txTime.text = timeLeftFormatted
        refDispensador.child("tiempo").setValue(timeLeftFormatted)
    }

    private fun navegaciónDispensador() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        refDispensador.removeEventListener(valueEventListener)
    }
}