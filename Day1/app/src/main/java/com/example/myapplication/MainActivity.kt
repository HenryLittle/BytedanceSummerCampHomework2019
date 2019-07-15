package com.example.myapplication

import android.os.Bundle
import android.support.design.button.MaterialButton
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.example.myapplication.day2.topRankIntent
import com.example.myapplication.day3.animIntent

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var count : Int = 0
    private val TAG:String = "[DEBUG]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Share ?", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val materialButton = findViewById<MaterialButton>(R.id.main_material_button)
        val radioButton = findViewById<RadioButton>(R.id.main_radio_button)
        val checkBox = findViewById<CheckBox>(R.id.main_check_box)
        val editText = findViewById<EditText>(R.id.main_edit_text)
        val textView = findViewById<TextView>(R.id.main_edit_text_text_view)
        val imageView = findViewById<ImageView>(R.id.main_image)

        materialButton.setOnClickListener {
            count ++
            Toast.makeText(this, "Material Button is pressed $count times.", Toast.LENGTH_SHORT ).show()
            Log.d(TAG, "Seems like our material button works just fine.")
        }

        radioButton.setOnClickListener {
            Toast.makeText(this, "Radio Button is checked", Toast.LENGTH_SHORT ).show()
            Log.w("[WARNING]", "Radio? Radio active warning!")
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Check Box is now $isChecked.", Toast.LENGTH_SHORT ).show()
            Log.d(TAG, "Check Box is a box, so we give it a logcat.")
        }

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                textView.text = s?.toString()?:"You have no input"
                Log.d(TAG, "Finish editing.")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "Before editing.")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "Editing.")
            }
        })

        imageView.setOnClickListener {
            Toast.makeText(this, "Pat the Shiba's head.", Toast.LENGTH_SHORT ).show()
            Log.d(TAG, "We shouldn't be obsessed with the puppy.")
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_top_rank -> {
                startActivity(topRankIntent())
            }
            R.id.nav_anim_test -> {
                startActivity(animIntent())
            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
