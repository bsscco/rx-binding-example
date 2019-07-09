package dev.bsscco.rxbinging

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setUpEventListeners()
        setUpRxBindings()
    }

    private var isButtonClickProcessing = false
    private fun setUpEventListeners() {
        val nicknameInputField = findViewById<EditText>(R.id.nicknameInputField)
        val passwordInputField = findViewById<EditText>(R.id.passwordInputField)
        val okButton = findViewById<Button>(R.id.okButton)

        nicknameInputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    // 입력 내용에 따라 버튼색 변하게 하기
                    if (text.length >= 4 && passwordInputField.length() >= 4) {
                        okButton.setBackgroundColor(Color.BLUE)
                    } else {
                        okButton.setBackgroundColor(Color.GRAY)
                    }
                }
            }
        })

        passwordInputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    // 입력 내용에 따라 버튼색 변하게 하기
                    if (text.length >= 4 && nicknameInputField.length() >= 4) {
                        okButton.setBackgroundColor(Color.BLUE)
                    } else {
                        okButton.setBackgroundColor(Color.GRAY)
                    }
                }
            }
        })

        okButton.setOnClickListener {
            // 버튼 클릭에 쿨타임 걸기
            if (isButtonClickProcessing) return@setOnClickListener
            isButtonClickProcessing = true
            Observable.timer(1, TimeUnit.SECONDS).subscribe { isButtonClickProcessing = false }

            when {
                nicknameInputField.length() < 4 -> Toast.makeText(
                    this,
                    "닉네임 4글자 이상!",
                    Toast.LENGTH_SHORT
                ).show()
                passwordInputField.length() < 4 -> Toast.makeText(
                    this,
                    "패스워드 4글자 이상!",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    nicknameInputField.setText("")
                    passwordInputField.setText("")
                    Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val compositeDisposable = CompositeDisposable()
    private fun setUpRxBindings() {
        compositeDisposable.add(Observable.merge(
            nicknameInputField.textChanges(),
            passwordInputField.textChanges()
        )
            .subscribe { text ->
                // 입력 내용에 따라 버튼색 변하게 하기
                if (nicknameInputField.length() >= 4 && passwordInputField.length() >= 4) {
                    okButton.setBackgroundColor(Color.BLUE)
                } else {
                    okButton.setBackgroundColor(Color.GRAY)
                }
            })

        compositeDisposable.add(
            okButton.clicks()
                .throttleFirst(1, TimeUnit.SECONDS) // 버튼 클릭에 쿨타임 걸기
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when {
                        nicknameInputField.length() < 4 -> Toast.makeText(
                            this,
                            "닉네임 4글자 이상!",
                            Toast.LENGTH_SHORT
                        ).show()
                        passwordInputField.length() < 4 -> Toast.makeText(
                            this,
                            "패스워드 4글자 이상!",
                            Toast.LENGTH_SHORT
                        ).show()
                        else -> {
                            nicknameInputField.setText("")
                            passwordInputField.setText("")
                            Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }, {
                    Log.d("BSSCCO", it.toString())
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
