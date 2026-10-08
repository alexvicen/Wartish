package com.vicen.webel.components.wartish.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.databinding.TresLineaBinding
import com.vicen.webel.components.wartish.entidades.Character
import com.vicen.webel.components.wartish.utils.ObjectBox
import java.util.*
import kotlin.concurrent.thread

class ThreeInRowActivity : AppCompatActivity() {
    private lateinit var binding: TresLineaBinding

    private val characterBox = ObjectBox.instance.boxStore.boxFor(Character::class.java)

    private val board = Array(8) { IntArray(8) }
    private val boardImg = Array(8) { arrayOfNulls<ImageView>(8) }

    private var rock: Int = 0
    private var log: Int = 0
    private var iron: Int = 0
    private var nugget: Int = 0
    private var uncutGem: Int = 0
    private var combo: Int = 0

    private var elementTouch = 0
    private var points = 20
    private var field1: ImageView? = null
    private var field2: ImageView? = null
    private var play = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TresLineaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initialize()
    }

    override fun onDestroy() {
        if (characterBox.all.isNotEmpty()) {
            val char = characterBox.all.first()
            char.rock += rock
            char.log += log
            char.iron += iron
            char.nugget += nugget
            char.uncutGem += uncutGem
            characterBox.put(char)
        }
        super.onDestroy()
    }

    private fun initialize() {
        createBoard()
        createBoardImages()
        createPlayThread()
        drawBoard()
        checkBoard()
        play = true
    }

    private fun createPlayThread() {
        Thread {
            // do your stuff
            runOnUiThread {
                // do onPostExecute stuff
            }
        }.start()
    }

    private fun createBoard() {
        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j] = Random().nextInt(5)
            }
        }
    }

    private fun createBoardImages() {
        for (i in boardImg.indices) {
            for (j in boardImg[i].indices) {
                val imageView = ImageView(this)
                when (board[i][j]) {
                    0 -> imageView.setImageResource(R.drawable.roca)
                    1 -> imageView.setImageResource(R.drawable.troncos)
                    2 -> imageView.setImageResource(R.drawable.hierro)
                    3 -> imageView.setImageResource(R.drawable.pepita)
                    else -> imageView.setImageResource(R.drawable.gema_bruto)
                }
                boardImg[i][j] = imageView
            }
        }
    }

    private fun drawBoard() {
        for (i in board.indices) {
            val ll = LinearLayout(this)
            ll.orientation = LinearLayout.HORIZONTAL
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.weight = 1f
            for (j in board[i].indices) {
                val iv: ImageView = boardImg[i][j]!!
                iv.tag = i.toString() + "" + j
                iv.layoutParams = lp
                iv.setOnClickListener {
                    onImageClick(it as ImageView)
                }
                ll.addView(iv)
            }
            binding.panelJuego.addView(ll)
        }
    }

    private fun onImageClick(v: ImageView) {
        elementTouch++
        if (elementTouch == 1) {
            combo = 0
            setCombo()
            binding.txtCombo.visibility = View.INVISIBLE
            field1 = v
            enabledNext()
        } else if (elementTouch == 2) {
            field2 = v
            if (field1 === field2) {
                elementTouch = 0
                enabledAll()
            } else {
                changePosition(field1, field2)
                binding.txtCombo.visibility = View.VISIBLE
                checkBoard()
                movements()
                enabledAll()
            }
        }
    }

    private fun movements() {
        points--
        binding.txtPuntos.text = points.toString()
        if (points == 0) {
            binding.panelJuego.visibility = View.INVISIBLE
        }
    }

    private fun changePosition(imageView1: ImageView?, imageView2: ImageView?) {
        val tag1 = imageView1?.tag.toString()
        val tag2 = imageView2?.tag.toString()
        val i1 = tag1.substring(0, 1).toInt()
        val j1 = tag1.substring(1).toInt()
        val i2 = tag2.substring(0, 1).toInt()
        val j2 = tag2.substring(1).toInt()
        val aux: Int = board[i1][j1]
        board[i1][j1] = board[i2][j2]
        board[i2][j2] = aux
        runOnUiThread { binding.panelJuego.removeAllViews() }
        runOnUiThread { createBoardImages() }
        runOnUiThread { drawBoard() }
    }

    private fun setCombo() {
        when {
            combo <= 2 -> {
                binding.txtCombo.setTextColor(Color.GREEN)
                binding.txtCombo.textSize = 20f
            }
            combo <= 5 -> {
                binding.txtCombo.setTextColor(Color.YELLOW)
                binding.txtCombo.textSize = 25f
            }
            else -> {
                binding.txtCombo.setTextColor(Color.RED)
                binding.txtCombo.textSize = 30f
            }
        }
        binding.txtCombo.text = getString(R.string.combo, combo)
    }

    private fun enabledNext() {
        val tag1: String = field1?.tag.toString()
        val i1 = tag1.substring(0, 1).toInt()
        val j1 = tag1.substring(1).toInt()
        for (i in boardImg.indices) {
            for (j in boardImg[i].indices) {
                if (i1 == i - 1 && j1 == j - 1 || i1 == i && j1 == j - 1 ||
                    i1 == i + 1 && j1 == j - 1 || i1 == i + 1 && j1 == j ||
                    i1 == i + 1 && j1 == j + 1 || i1 == i && j1 == j + 1 ||
                    i1 == i - 1 && j1 == j + 1 || i1 == i - 1 && j1 == j ||
                    i1 == i && j1 == j
                ) continue
                boardImg[i][j]?.isClickable = false
                boardImg[i][j]?.alpha = 0.5f

            }
        }
    }

    private fun enabledAll() {
        for (i in boardImg.indices) {
            for (j in boardImg[i].indices) {
                boardImg[i][j]?.isClickable = true
                boardImg[i][j]?.alpha = 1f
            }
        }
    }

    private fun checkBoard() {
        thread {
            while (checkHorizontal()) {
            }
        }
    }


    private fun checkHorizontal(): Boolean {
        var cont = 0
        val r = Random()
        for (i in board.indices) {
            for (j in 1 until board[i].size) {
                if (board[i][j - 1] == board[i][j]) {
                    cont++
                    if (cont == 2) {
                        cont = 0
                        if (play) {
                            addPoints(board[i][j])
                            combo++
                            setCombo()
                        }
                        changePosition(
                            boardImg[i][j - 1],
                            boardImg[r.nextInt(board.size)][r.nextInt(board.size)]
                        )
                        changePosition(
                            boardImg[i][j],
                            boardImg[r.nextInt(board.size)][r.nextInt(board.size)]
                        )
                        changePosition(
                            boardImg[i][j],
                            boardImg[r.nextInt(board.size)][r.nextInt(board.size)]
                        )
                        return true
                    }
                } else {
                    cont = 0
                }
            }
        }
        return checkVertical()
    }

    private fun checkVertical(): Boolean {
        var cont = 0
        val r = Random()
        for (i in board.indices) {
            for (j in 1 until board[i].size) {
                if (board[j - 1][i] == board[j][i]) {
                    cont++
                    if (cont == 2) {
                        cont = 0
                        if (play) {
                            addPoints(board[j][i])
                            combo++
                            setCombo()
                        }
                        changePosition(
                            boardImg[j - 1][i],
                            boardImg[r.nextInt(board.size)][r.nextInt(board.size)]
                        )
                        changePosition(
                            boardImg[j][i],
                            boardImg[r.nextInt(board.size)][r.nextInt(board.size)]
                        )
                        changePosition(
                            boardImg[j][i],
                            boardImg[r.nextInt(board.size)][r.nextInt(board.size)]
                        )
                        return true
                    }
                } else {
                    cont = 0
                }
            }
        }
        return false
    }

    private fun addPoints(t: Int) {
        when (t) {
            0 -> {
                rock += 3
                binding.txtRoca.text = rock.toString()
            }
            1 -> {
                log += 3
                binding.txtTronco.text = log.toString()
            }
            2 -> {
                iron += 3
                binding.txtHierro.text = iron.toString()
            }
            3 -> {
                nugget += 3
                binding.txtPepita.text = nugget.toString()
            }
            4 -> {
                uncutGem += 3
                binding.txtGemaBruto.text = uncutGem.toString()
            }

        }
    }

}