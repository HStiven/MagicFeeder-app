package FirstApp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.singuploginfirebase.MenuActivity
import com.example.singuploginfirebase.R


class PrimeraAppActivity : AppCompatActivity() {
    private var isMachoSelect: Boolean = true
    private var isFemaleSelect: Boolean = false
    private var genderSelection: String = "Macho"

    private lateinit var machoView: CardView
    private lateinit var femaleView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primera_app)
        val atrasInicio = findViewById<AppCompatButton>(R.id.backInicio)
        atrasInicio.setOnClickListener {
            navegaciónDispensador()
        }

        machoView = findViewById(R.id.machoView)
        femaleView = findViewById(R.id.femaleView)
        val boton = findViewById<Button>(R.id.boton)

        iniciarListeners()
        generarColor()

        boton.setOnClickListener { navegacion() }

        val genderItems = listOf("Macho", "Hembra")
        val genderAutoComplete: AutoCompleteTextView = findViewById(R.id.second_auto_complete)
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderItems)
        genderAutoComplete.setAdapter(genderAdapter)

        genderAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                genderSelection = genderAdapter.getItem(position).toString()
                Toast.makeText(this, "Item: $genderSelection", Toast.LENGTH_SHORT).show()
            }
    }

    private fun iniciarListeners() {
        machoView.setOnClickListener {
            changeGender()
            generarColor()
            validarSeleccion()
        }
        femaleView.setOnClickListener {
            changeGender()
            generarColor()
            validarSeleccion()
        }
    }

    private fun navegaciónDispensador() {
        intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun validarSeleccion() {
        if (genderSelection.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un género de mascota", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isMachoSelect && !isFemaleSelect) {
            Toast.makeText(this, "Debe seleccionar el tipo de mascota (macho o hembra)", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun changeGender() {
        isMachoSelect = !isMachoSelect
        isFemaleSelect = !isFemaleSelect
    }

    private fun generarColor() {
        machoView.setCardBackgroundColor(seleccionarColor(isMachoSelect))
        femaleView.setCardBackgroundColor(seleccionarColor(isFemaleSelect))
    }

    private fun seleccionarColor(isSelectComponentColor: Boolean): Int {
        val realColor = if (isSelectComponentColor) {
            R.color.white
        } else {
            R.color.magicfeeder_500
        }
        return ContextCompat.getColor(this, realColor)
    }

    private fun navegacion() {
        if (!isMachoSelect && !isFemaleSelect) {
            Toast.makeText(this, "Debe seleccionar el tipo de mascota (macho o hembra)", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = when (genderSelection) {
            "Macho" -> {
                if (isMachoSelect) {
                    Intent(this, FirstApp.MainActivity4::class.java)
                } else {
                    Intent(this, FirstApp.MainActivity2::class.java)
                }
            }
            "Hembra" -> {
                if (isFemaleSelect) {
                    Intent(this, FirstApp.MainActivity3::class.java)
                } else {
                    Intent(this, FirstApp.MainActivity5::class.java)
                }
            }
            else -> throw IllegalArgumentException("Gender selection not recognized")
        }
        startActivity(intent)
    }
}