package com.example.relaxingmotion_proba

import android.os.*
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.example.relaxingmotion_proba.databinding.ActivityMainBinding
import com.google.android.material.bottomappbar.BottomAppBar
import java.lang.Thread.sleep
import kotlin.math.round
import kotlin.math.sqrt


class MainActivity: AppCompatActivity() {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    // Binding
    lateinit var binding: ActivityMainBinding
    // Bottom navigation menu
    private var isMain: Boolean = false
    private var isFABButtonsGroupView: Boolean = false
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Подключение Binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Методы работы с Bottom Navigation Menu
        setBottomNavigationMenu()

        // Отображение содержимого макета
        setContentView(binding.root)
    }

    //region УСТАНОВКА BOTTOM NAVIGATION MENU
    private fun setBottomAppBar() {
        this.setSupportActionBar(binding.bottomNavigationMenu.bottomAppBar)
//        setHasOptionsMenu(true)

        switchBottomAppBar(this)
        binding.bottomNavigationMenu.bottomAppBarFab.setOnClickListener {
            switchBottomAppBar(this)
        }
    }
    // Переключение режима нижней навигационной кнопки BottomAppBar
    // с центрального на крайнее левое положение и обратно
    fun switchBottomAppBar(context: MainActivity) {
        if (isMain) {
            // Изменение нижего меню, выходящего из FAB
            if (isFABButtonsGroupView) {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            }
            // Изменение нижней кнопки FAB
            isMain = false
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon = null
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode =
                BottomAppBar.FAB_ALIGNMENT_MODE_END
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_back_fab)
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(
                R.menu.bottom_menu_bottom_bar_other_screen)
        } else {
            // Изменение нижего меню, выходящего из FAB
            if (isFABButtonsGroupView) {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            }
            // Изменение нижней кнопки FAB
            isMain = true
            binding.bottomNavigationMenu.bottomAppBar.navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
            binding.bottomNavigationMenu.bottomAppBar.fabAlignmentMode =
                BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            binding.bottomNavigationMenu.bottomAppBarFab.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_plus_fab)
            )
            binding.bottomNavigationMenu.bottomAppBar.replaceMenu(R.menu.bottom_menu_bottom_bar)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Отобразить справа внизу стартового меню
        menuInflater.inflate(R.menu.bottom_menu_bottom_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bottom_bar_settings -> {}
            android.R.id.home -> {}
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region МЕТОДЫ ДЛЯ НАСТРОЙКИ КНОПОК BOTTOM NAVIGATION MENU
    private fun setBottomNavigationMenu() {
        // Установка Bottom Navigation Menu
        setBottomAppBar()
        // Установка слушателя на длительное нажатие на нижнюю кнопку FAB
        binding.fabButtonsGroup.visibility = View.INVISIBLE
        binding.bottomNavigationMenu.bottomAppBarFab.setOnLongClickListener {
            if (isFABButtonsGroupView) {
                binding.fabButtonsGroup.visibility = View.INVISIBLE
                isFABButtonsGroupView = !isFABButtonsGroupView
            } else {
                // Анимация появления кнопок меню из нижней кнопки FAB
                if (isMain) {
                    val constraintLayout =
                        findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.constrainCircle(
                        R.id.fab_button_day_photo,
                        R.id.bottom_fab_maket,
                        0,
                        285f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_wiki,
                        R.id.bottom_fab_maket,
                        0,
                        330f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_nasa_archive,
                        R.id.bottom_fab_maket,
                        0,
                        20f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_settings,
                        R.id.bottom_fab_maket,
                        0,
                        73f
                    )
                    constraintSet.applyTo(constraintLayout)
                    binding.fabButtonsGroup.visibility = View.VISIBLE
                    isFABButtonsGroupView = !isFABButtonsGroupView
                    Thread {
                        // Исходные параметры
                        val numberFrames: Int = 30 // numberFrames - ОБЩЕЕ КОЛИЧЕСТВО ШАГОВ (С УЧЁТОМ РЕЛАКСАЦИИ)
                        val deltaTime: Long = 8L // deltaTime - ВАЖНЫЙ ПАРАМТЕР - ДЛИТЕЛЬНОСТЬ ОДНОГО ШАГА
                        val deltaRadius: Int = 8 // deltaRadius - ВАЖНЫЙ ПАРАМЕТР, ОТВЕЧАЕТ ЗА УВЕЛИЧЕНИЕ РАДИУСА НА ОДНОМ ШАГЕ
                        val handler = Handler(Looper.getMainLooper())

                        // Создание релаксации при прохождении через конечную точку
                        val a: Double = 1.0 // a - В ПРИНЦИПЕ, МОЖНО ЭТОТ ПАРАМЕТР ИСКЛЮЧИТЬ, ПРИРАВНЯВ ЕГО К ЕДИНИЦЕ. ОН ОТВЕЧАЕТ В УРАВНЕНИИ y = a * x2 за широту нашей параболы
                        val k: Double = 0.3 // k - ОЧЕНЬ ВАЖНЫЙ ПАРАМЕТР (0 <= k <= 1). ОТВЕЧАЕТ ЗА ТО, КАК ДАЛЕКО ПРОЙДЕТ ОБЪЕКТ, ПО СРАВНЕНИЮ С ПРОЙДЕННЫМ ДО КОНЕЧНОЙ ТОЧКИ ПУТИ. 1 - ПУТЬ ВОЗВРАТА БУДЕТ РАВЕН ПУТИ ДО КОНЕЧНОЙ ТОКИ
                        var y: Double = (numberFrames * deltaRadius).toDouble() // Начальная точка движения для определения maxX и minX. После их определения y корректируется
                        val maxX: Double = sqrt(y / a) // Расстояние до конечной точки
                        val minX: Double = sqrt(y * k / a) // Длина траектории релаксации
                        val deltaX: Double = (maxX + minX) / numberFrames // Смещение на одном шаге
                        y *= (1 + k) // Учёт длины траектории релаксации (нужно увеличить начальный y, чтобы мы в результате удалились только на maxX от начальной точки

                        repeat(numberFrames) {
                            sleep(deltaTime)
                            handler.post {
                                val constraintLayout =
                                    findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                                val constraintSet = ConstraintSet()
                                constraintSet.clone(constraintLayout)
                                constraintSet.constrainCircle(
                                    R.id.fab_button_day_photo,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    285f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_wiki,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    330f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_nasa_archive,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    20f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_settings,
                                    R.id.bottom_fab_maket,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    73f
                                )
                                constraintSet.applyTo(constraintLayout)
                            }
                        }
                    }.start()
                } else {
                    val constraintLayout =
                        findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.constrainCircle(
                        R.id.fab_button_day_photo,
                        R.id.bottom_fab_maket_right,
                        0,
                        285f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_wiki,
                        R.id.bottom_fab_maket_right,
                        0,
                        310f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_search_in_nasa_archive,
                        R.id.bottom_fab_maket_right,
                        0,
                        345f
                    )
                    constraintSet.constrainCircle(
                        R.id.fab_button_settings,
                        R.id.bottom_fab_maket_right,
                        0,
                        20f
                    )
                    constraintSet.applyTo(constraintLayout)
                    binding.fabButtonsGroup.visibility = View.VISIBLE
                    isFABButtonsGroupView = !isFABButtonsGroupView
                    Thread {
                        // Исходные параметры
                        val numberFrames: Int = 30 // numberFrames - ОБЩЕЕ КОЛИЧЕСТВО ШАГОВ (С УЧЁТОМ РЕЛАКСАЦИИ)
                        val deltaTime: Long = 8L // deltaTime - ВАЖНЫЙ ПАРАМТЕР - ДЛИТЕЛЬНОСТЬ ОДНОГО ШАГА
                        val deltaRadius: Int = 8 // deltaRadius - ВАЖНЫЙ ПАРАМЕТР, ОТВЕЧАЕТ ЗА УВЕЛИЧЕНИЕ РАДИУСА НА ОДНОМ ШАГЕ
                        val handler = Handler(Looper.getMainLooper())

                        // Создание релаксации при прохождении через конечную точку
                        val a: Double = 1.0 // a - В ПРИНЦИПЕ, МОЖНО ЭТОТ ПАРАМЕТР ИСКЛЮЧИТЬ, ПРИРАВНЯВ ЕГО К ЕДИНИЦЕ. ОН ОТВЕЧАЕТ В УРАВНЕНИИ y = a * x2 за широту нашей параболы
                        val k: Double = 0.2 // k - ОЧЕНЬ ВАЖНЫЙ ПАРАМЕТР (0 <= k <= 1). ОТВЕЧАЕТ ЗА ТО, КАК ДАЛЕКО ПРОЙДЕТ ОБЪЕКТ, ПО СРАВНЕНИЮ С ПРОЙДЕННЫМ ДО КОНЕЧНОЙ ТОЧКИ ПУТИ. 1 - ПУТЬ ВОЗВРАТА БУДЕТ РАВЕН ПУТИ ДО КОНЕЧНОЙ ТОКИ
                        var y: Double = (numberFrames * deltaRadius).toDouble() // Начальная точка движения для определения maxX и minX. После их определения y корректируется
                        val maxX: Double = sqrt(y / a) // Расстояние до конечной точки
                        val minX: Double = sqrt(y * k / a) // Длина траектории релаксации
                        val deltaX: Double = (maxX + minX) / numberFrames // Смещение на одном шаге
                        y *= (1 + k) // Учёт длины траектории релаксации (нужно увеличить начальный y, чтобы мы в результате удалились только на maxX от начальной точки

                        repeat(numberFrames) {
                            sleep(deltaTime)
                            handler.post {
                                val constraintLayout =
                                    findViewById<ConstraintLayout>(R.id.fab_buttons_container)
                                val constraintSet = ConstraintSet()
                                constraintSet.clone(constraintLayout)
                                constraintSet.constrainCircle(
                                    R.id.fab_button_day_photo,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    285f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_wiki,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    310f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_search_in_nasa_archive,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    345f
                                )
                                constraintSet.constrainCircle(
                                    R.id.fab_button_settings,
                                    R.id.bottom_fab_maket_right,
                                    round(y - a * (maxX - deltaX * it) *
                                            (maxX - deltaX * it)).toInt(),
                                    20f
                                )
                                constraintSet.applyTo(constraintLayout)
                            }
                        }
                    }.start()
                }
            }
            true
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с картинкой дня
        binding.fabButtonsContainer.getViewById(R.id.fab_button_day_photo).setOnClickListener {
            Toast.makeText(this, "Картинка дня", Toast.LENGTH_SHORT).show()
        }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в Википедии
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_wiki)
            .setOnClickListener {
                Toast.makeText(this, "Поиск в Википедии", Toast.LENGTH_SHORT).show()
            }
        // Установка слушателя на нажатие кнопки вызова фрагмента с поиском в архиве NASA
        binding.fabButtonsContainer.getViewById(R.id.fab_button_search_in_nasa_archive)
            .setOnClickListener {
                Toast.makeText(this, "Поиск в архиве NASA", Toast.LENGTH_SHORT).show()
            }
        // Установка слушателя на нажатие кнопки вызова настроек приложения
        binding.fabButtonsContainer.getViewById(R.id.fab_button_settings).setOnClickListener {
            Toast.makeText(this, "Настройки приложения", Toast.LENGTH_SHORT).show()
        }
    }
    //endregion
}