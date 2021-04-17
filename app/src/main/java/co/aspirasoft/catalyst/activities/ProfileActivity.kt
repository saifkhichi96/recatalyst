package co.aspirasoft.catalyst.activities

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.bo.AccountsBO
import co.aspirasoft.catalyst.bo.AuthBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.ConnectionsDao
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.databinding.ActivityProfileBinding
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.AccountStorage
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.util.InputUtils.isNotBlank
import com.snapchat.kit.sdk.SnapLogin
import kotlinx.coroutines.launch


/**
 * Shows details of a [UserAccount].
 *
 * This is a profile page, where a user can see their own or someone
 * else's details.
 */
class ProfileActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var user: UserAccount
    private lateinit var accountStorage: AccountStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getSerializableExtra(MyApplication.EXTRA_PROFILE_USER) as UserAccount? ?: currentUser
        accountStorage = AccountStorage(this, user.id)
    }

    override fun updateUI(currentUser: UserAccount) {
        binding.user = user
        binding.isOwnProfile = user == currentUser

        // AVATAR
        ImageLoader.loadUserAvatar(user.id, binding.accountAvatar)

        // STATS SECTION
        ConnectionsDao.getUserConnections(user.id) { binding.connections = it.size }
        ProjectsDao.getUserProjects(user.id) { binding.projects = it.size }

        // ACCOUNT SETTINGS
        // TODO: Allow password reset
        // Allow account deletion
        binding.deleteAccountButton.setOnClickListener { binding.deleteSection.visibility = View.VISIBLE }
        binding.cancelDeleteButton.setOnClickListener { binding.deleteSection.visibility = View.GONE }
        binding.confirmDeleteButton.setOnClickListener { onDeleteAccountClicked() }

        // CONNECT SECTION
        // TODO: Show Connect button only if user not a Connection
    }

    private fun onDeleteAccountClicked() {
        if (binding.passwordField.isNotBlank(true)) {
            try {
                binding.confirmDeleteButton.isEnabled = false
                // Delete user data from database
                AccountsDao.delete(user.id) {
                    lifecycleScope.launch {
                        val password = binding.passwordField.text.toString().trim()
                        AccountsBO.deleteAccount(user.email, password)
                        AuthBO.signOut()
                        finish()
                    }
                }
            } catch (ex: Exception) {
                binding.passwordField.error = ex.message
            } finally {
                binding.confirmDeleteButton.isEnabled = true
            }
        }
    }

    fun changeAvatar(v: View) {
        startSecurely(ProfileAvatarActivity::class.java)
    }

}