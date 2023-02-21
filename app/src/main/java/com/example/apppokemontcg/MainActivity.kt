package com.example.myapplication


import PokemonCardAdapter
import android.app.Activity
import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppokemontcg.*
import com.example.apppokemontcg.databinding.MainActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.PUT

class MainActivity : AppCompatActivity() {
    private lateinit var rvMain: RecyclerView

    private lateinit var binding: MainActivityBinding

    private var lista = mutableListOf<PokemonCard>()
    private lateinit var miAdapter: PokemonCardAdapter


    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var listaCopia: MutableList<Pokemon>

    private lateinit var context: Context

    private lateinit var pokemonService: PokemonService
    private var pageNumber = 0
    private var totalPages = 0
    private var allCards = mutableListOf<PokemonCard>()

    private lateinit var spinner: Spinner

    private var isLoading = false
    private var isLastPage = false

    private lateinit var btnPOST: Button
    private lateinit var btnDELETE: Button
    private lateinit var btnPUT: Button

    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvNo.text = "Cargando ..."



        btnPOST = binding.btnPOST
        btnDELETE = binding.btnDELETE
        btnPUT = binding.btnPUT

        rvMain = findViewById(R.id.rvMain)
        totalPages = 1

        searchView = binding.searchview

        miAdapter = PokemonCardAdapter(allCards)


        layoutManager = LinearLayoutManager(applicationContext)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pokemontcg.io/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val pokemonService = retrofit.create(PokemonService::class.java)
        binding.tvNo.visibility = View.VISIBLE
// Se crea un Listener para la respuesta de la llamada a la API
        pokemonService.getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", totalPages)
            .enqueue(object : Callback<PokemonResponse> {
                // Se define la acción a realizar en caso de éxito en la llamada
                override fun onResponse(
                    call: Call<PokemonResponse>,
                    response: Response<PokemonResponse>
                ) {
                    val pokemons = response.body()?.cards
                    allCards.addAll(pokemons!!)

                    if (response.isSuccessful) {
                        binding.tvNo.visibility = View.INVISIBLE
                        miAdapter.setList(allCards)

                        binding.rvMain.layoutManager = layoutManager
                        binding.rvMain.adapter = miAdapter

                    }


                }

                override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                    binding.tvNo.visibility = View.VISIBLE
                    binding.tvNo.text = "No hay pokemon"
                }
            })


        // Se define la acción a realizar en caso de fallo en la llamada
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                pokemonService.getCarta("c099e29e-2bde-4974-a532-cb7e2cf90072", newText!!)
                    .enqueue(object : Callback<PokemonResponse> {
                        override fun onResponse(
                            call: Call<PokemonResponse>,
                            response: Response<PokemonResponse>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()!!.cards
                                miAdapter.setList(result as MutableList<PokemonCard>)
                            }
                        }

                        override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "No se pudo realizar la búsqueda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                return true
            }
        })

        setUpScrollListener()
        listenersBotones()

    }


    fun listenersBotones() {

        btnPOST.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.147.2:8080/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val myApi = retrofit.create(SamirService::class.java)

                val myData = SamirClass(
                    "Galindillo", 20, "samir feo", 10, 5, "el ejido",
                    "institutos", "2022-01-27", 4, 3, 40
                )

                myApi.postMyData(myData).enqueue(object : Callback<SamirClass> {

                    override fun onFailure(call: Call<SamirClass>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<SamirClass>,
                        response: Response<SamirClass>
                    ) {
                        if (response.isSuccessful) {
                            val myResponse = response.body()
                            Log.i("POST", "realizado con id: " + myResponse?.idInmueble.toString())
                        } else {
                            System.err.println(response.errorBody()?.string())
                        }
                    }


                })

            }


        }

        btnDELETE.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ingresa un número")

// Configura el cuadro de texto de entrada
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

// Agrega el botón "Aceptar"
            builder.setPositiveButton("Aceptar") { dialog, which ->
                val number = input.text.toString().toInt()


                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.147.2:8080/api/inmuebles/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val myApi = retrofit.create(SamirService::class.java)

                myApi.deleteInmueble(input.text.toString()).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.i("delete", "delete completado")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.i("delete", "delete error")
                    }

                })

            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }

            builder.show()


        }

        btnPUT.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ingresa un número")

// Configura el cuadro de texto de entrada
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

// Agrega el botón "Aceptar"
            builder.setPositiveButton("Aceptar") { dialog, which ->
                val number = input.text.toString().toInt()


                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.147.2:8080/api/inmuebles/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val myApi = retrofit.create(SamirService::class.java)
                val myData = SamirClass(
                    "Galindillo", 20, "samir guapo", 10, 5, "el ejido",
                    "institutos", "2022-01-27", 4, 3, 40
                )
                var ruta= ""+number
                myApi.putInmueble(ruta, myData).enqueue(object : Callback<SamirClass> {
                    override fun onResponse(call: Call<SamirClass>, response: Response<SamirClass>) {
                       Log.i("usuario", "usuario añadido")
                    }

                    override fun onFailure(call: Call<SamirClass>, t: Throwable) {
                        Log.i("usuario", "usuario añadido")
                    }
                })

            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }

            builder.show()


        }
    }

    private fun setUpScrollListener() {

        binding.rvMain.setOnScrollChangeListener { _, _, _, _, _ ->
            val totalItemCount = binding.rvMain.computeVerticalScrollRange()
            val visibleItemCount = binding.rvMain.computeVerticalScrollExtent()
            val pastVisibleItems = binding.rvMain.computeVerticalScrollOffset()

            if (pastVisibleItems + visibleItemCount >= totalItemCount * 0.60) {
                addNextN()
            }
        }


    }

    fun addNextN() {

        if (pageNumber < 137) {


            CoroutineScope(Dispatchers.IO).launch {

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.pokemontcg.io/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val call = retrofit.create(PokemonService::class.java)
                    .getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", ++totalPages)
                    .enqueue(object : Callback<PokemonResponse> {
                        override fun onResponse(
                            call: Call<PokemonResponse>,
                            response: Response<PokemonResponse>
                        ) {
                            runOnUiThread {

                                if (call.isExecuted) {
                                    val pokemon = response.body()?.cards
                                    allCards.addAll(pokemon!!)
                                    miAdapter.notifyDataSetChanged()
                                } else {
                                    println("error")
                                }

                            }
                        }

                        override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                            println("error")
                        }

                    })


            }

        }

    }


    fun spinnerListener() {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.rvMain.visibility = View.INVISIBLE
                binding.tvNo.visibility = View.VISIBLE
                binding.tvNo.text = "Cargando ..."
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.pokemontcg.io/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val pokemonService = retrofit.create(PokemonService::class.java)
                val selectedItem = spinner.getItemAtPosition(position) as Int
                pokemonService.getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", selectedItem)
                    .enqueue(object : Callback<PokemonResponse> {
                        override fun onResponse(
                            call: Call<PokemonResponse>,
                            response: Response<PokemonResponse>
                        ) {
                            allCards.clear()
                            miAdapter.setList(allCards)
                            val layoutManager = LinearLayoutManager(applicationContext)
                            binding.rvMain.layoutManager = layoutManager
                            binding.rvMain.adapter = miAdapter
                            val pokemons = response.body()?.cards
                            allCards.addAll(pokemons!!)

                            if (response.isSuccessful) {
                                binding.rvMain.visibility = View.VISIBLE
                                binding.tvNo.visibility = View.INVISIBLE
                                miAdapter.setList(allCards)
                                val layoutManager = LinearLayoutManager(applicationContext)
                                binding.rvMain.layoutManager = layoutManager
                                binding.rvMain.adapter = miAdapter
                            }
                        }

                        override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                            binding.tvNo.visibility = View.VISIBLE
                            binding.tvNo.text = "No hay pokemon"
                        }
                    })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no se realiza ninguna acción
            }
        }


    }

    private fun cargarSpinner() {

        val numbers = ArrayList<Int>()
        for (i in 1..137) {
            numbers.add(i)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

    private fun buscarPokemon(texto: String?) {
        val resultados = allCards.filter {
            it.name.contains(texto!!, true)
        }
        miAdapter.setList((resultados as MutableList<PokemonCard>))

    }

    private fun cargarTodosLosPokemon() {
        while (pageNumber <= totalPages) {
            pokemonService.getCards("c099e29e-2bde-4974-a532-cb7e2cf90072", pageNumber)
                .enqueue(object : Callback<PokemonResponse> {
                    // Se define la acción a realizar en caso de éxito en la llamada
                    override fun onResponse(
                        call: Call<PokemonResponse>,
                        response: Response<PokemonResponse>
                    ) {
                        // Se obtiene la lista de pokemones
                        val pokemons = response.body()?.cards
                        allCards.addAll(pokemons!!)
                        pageNumber++
                    }

                    override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    // Se define la acción a realizar en caso de fallo en la llamada

                })
        }
        miAdapter.setList(allCards)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvMain.layoutManager = layoutManager
        binding.rvMain.adapter = miAdapter
    }


}