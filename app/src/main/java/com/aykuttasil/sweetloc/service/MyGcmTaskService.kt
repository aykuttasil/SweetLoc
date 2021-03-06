/* Author - Aykut Asil(@aykuttasil) */
package com.aykuttasil.sweetloc.service

import android.os.Bundle

import com.aykuttasil.sweetloc.app.Const
import com.aykuttasil.sweetloc.model.event.ErrorEvent
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams

import org.greenrobot.eventbus.EventBus

class MyGcmTaskService : GcmTaskService() {

    override fun onInitializeTasks() {
    }

    override fun onRunTask(taskParams: TaskParams): Int {
        try {
            val bundle = taskParams.extras

            return when (bundle.getString(Const.TASK_TYPE)) {
                Const.TASK_LOCATION -> {
                    SchedulerLocationTask(bundle)
                }
                else -> {
                    GcmNetworkManager.RESULT_SUCCESS
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val errorEvent = ErrorEvent()
            errorEvent.errorContent = e.message
            EventBus.getDefault().post(errorEvent)
            return GcmNetworkManager.RESULT_RESCHEDULE
        }
    }

    private fun SchedulerLocationTask(bundle: Bundle): Int {
        /*
        String jsonModel = null;
        String uuid = null;
        ModelGcmTask modelGcmTask = null;
        try {
            jsonModel = bundle.getString(Const.REAKTIF_TASK_BUNDLE);
            uuid = bundle.getString(Const.REAKTIF_TASK_UUID);
            modelGcmTask = DbManager.getGcmTaskFromId(uuid);

            ZiyaretRequest request = new Gson().fromJson(jsonModel, ZiyaretRequest.class);
            request.setRequestType(ReaktifRequest.REAKTIF_REQUEST_TYPE_GCM);
            request.setProcessDate(SweetLocHelper.getFormatTime());
            ZiyaretResponse ziyaretResponse = sendZiyaretProcess(request);
            if (ziyaretResponse != null) {
                switch (ziyaretResponse.getCode()) {
                    case 0: {
                        EventBus.getDefault().post(ziyaretResponse);
                        NotificationHelper mNotificationHelper = NotificationHelper.newInstance(getApplicationContext(), 1)
                                .setNotifContent(request.getGonderiNo() + " numaralı gönderinin Ziyaret işlemi tamamlanmıştır.")
                                .setNotifTitle("Ziyaret")
                                .setNotifIcon(R.drawable.ic_stat_editor_attach_file)
                                .setNotifId((new Random().nextInt() + new Random().nextInt()))
                                .buildNotif();
                        mNotificationHelper.showNotif();

                        ReaktifApiManager.getInstance(getApplicationContext()).SignIn();
                        if (modelGcmTask != null) {
                            modelGcmTask.delete();
                            EventBus.getDefault().post(modelGcmTask);
                        }
                        return GcmNetworkManager.RESULT_SUCCESS;
                    }
                }
            }
        } catch (Exception e) {
            SweetLocHelper.CrashlyticsLog(e);
            e.printStackTrace();
        }

        modelGcmTask.setTaskTryCount(modelGcmTask.getTaskTryCount() + 1);
        modelGcmTask.setTaskDate(SweetLocHelper.getFormatTime());
        modelGcmTask.save();

        if (modelGcmTask.getTaskTryCount() == Const.REAKTIF_TASK_TRY_COUNT) {
            modelGcmTask.setTaskStatus(Const.REAKTIF_TASK_STATUS_IPTAL);
            modelGcmTask.save();
            EventBus.getDefault().post(modelGcmTask);
            return GcmNetworkManager.RESULT_FAILURE;
        }
        EventBus.getDefault().post(modelGcmTask);
        return GcmNetworkManager.RESULT_RESCHEDULE;
        */
        return GcmNetworkManager.RESULT_SUCCESS
    }

    companion object {

        private val TAG = MyGcmTaskService::class.java.simpleName
    }

    // ////////////

    /*
    private ZiyaretResponse sendZiyaretProcess(ZiyaretRequest ziyaretRequest) {
        return ReaktifApiManager.getInstance(getApplicationContext()).ZiyaretSync(ziyaretRequest);
    }
    */
}
