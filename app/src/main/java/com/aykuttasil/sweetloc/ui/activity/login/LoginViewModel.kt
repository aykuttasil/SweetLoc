package com.aykuttasil.sweetloc.ui.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aykuttasil.sweetloc.data.DataManager
import com.aykuttasil.sweetloc.model.process.DataOkDialog
import com.aykuttasil.sweetloc.util.RxAwareViewModel
import com.aykuttasil.sweetloc.util.SingleLiveEvent
import com.google.firebase.auth.FirebaseUser
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by aykutasil on 22.01.2018.
 */
class LoginViewModel @Inject constructor(private val dataManager: DataManager) : RxAwareViewModel() {

    val observableSnackBar = SingleLiveEvent<String>()
    val observableOkDialog = SingleLiveEvent<DataOkDialog>()
    private val observableLoginUser = MutableLiveData<FirebaseUser>()
    private val observableRegisterUser = MutableLiveData<FirebaseUser>()

    fun login(email: String, password: String): LiveData<FirebaseUser> {
        disposables.add(dataManager.loginUser(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.i("Kullanıcı girişi başarılı.")
                    observableSnackBar.value = "${it?.email} ile oturum açıldı."
                    observableLoginUser.value = it
                }, {
                    it.printStackTrace()
                    observableOkDialog.value = DataOkDialog("SweetLoc", it?.message ?: "") {}
                })
        )

        return observableLoginUser;
    }

    fun register(email: String, password: String): LiveData<FirebaseUser> {
        disposables.add(dataManager.registerUser(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.i("Kullanıcı kaydı başarılı.")
                    observableSnackBar.value = "${it?.email} ile kayıt olundu."
                    observableRegisterUser.value = it
                }, {
                    it.printStackTrace()
                    observableOkDialog.value = DataOkDialog("SweetLoc", it?.message ?: "") {}
                })
        )

        return observableRegisterUser
    }
}