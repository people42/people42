package com.cider.fourtytwo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.cider.fourtytwo.databinding.ActivitySettingsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener

class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.web_client_id)
            .requestServerAuthCode(BuildConfig.web_client_id)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "설정"
        supportActionBar?.elevation = 0.0F  // 상자 그림자 삭제

        binding.privacyPolicy.setOnClickListener {
            if(binding.privacyPolicyWebview.visibility == VISIBLE) {
                binding.privacyPolicyWebview.visibility = GONE
//                binding.layoutBtn01.animate().apply {
//                    duration = 300
//                    rotation(0f)
//                }
            } else {
                binding.privacyPolicyWebview.visibility = VISIBLE
                binding.termsConditionsWebview.visibility = GONE
//                binding.layoutBtn01.animate().apply {
//                    duration = 300
//                    rotation(180f)
//                }
            }
        }
        binding.termsConditions.setOnClickListener {
            if (binding.termsConditionsWebview.visibility == View.VISIBLE) {
                binding.termsConditionsWebview.visibility = View.GONE
//                binding.layoutBtn01.animate().apply {
//                    duration = 300
//                    rotation(0f)
//                }
            } else {
                binding.termsConditionsWebview.visibility = View.VISIBLE
                binding.privacyPolicyWebview.visibility = GONE

//                binding.layoutBtn01.animate().apply {
//                    duration = 300
//                    rotation(180f)
//                }
            }
        }
        binding.signout.setOnClickListener{
            binding.privacyPolicyWebview.visibility = GONE
            binding.termsConditionsWebview.visibility = View.GONE
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, OnCompleteListener {
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                })
        }
        binding.withdrawal.setOnClickListener{
            binding.privacyPolicyWebview.visibility = GONE
            binding.termsConditionsWebview.visibility = View.GONE
            mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, OnCompleteListener {
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                })
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}