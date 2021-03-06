package com.jaaaelu.gzw.neteasy.evernote.task;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.evernote.client.android.helper.Cat;
import com.evernote.edam.error.EDAMUserException;

import net.vrallev.android.task.Task;
import net.vrallev.android.task.TaskExecutor;

import java.util.concurrent.Executors;

/**
 * @author rwondratschek
 */
abstract class BaseTask<RESULT> extends Task<RESULT> {

    private static final Cat CAT = new Cat("BaseTask");

    private static final TaskExecutor TASK_EXECUTOR = new TaskExecutor.Builder()
            .setExecutorService(Executors.newFixedThreadPool(12))
            .build();

    private final Class<RESULT> mResultClass;

    BaseTask(Class<RESULT> resultClass) {
        mResultClass = resultClass;
    }

    public void start(Activity activity) {
        TASK_EXECUTOR.execute(this, activity);
    }

    public void start(Activity activity, String annotationId) {
        TASK_EXECUTOR.execute(this, activity, annotationId);
    }

    public void start(Fragment fragment) {
        TASK_EXECUTOR.execute(this, fragment);
    }

    public void start(Fragment fragment, String annotationId) {
        TASK_EXECUTOR.execute(this, fragment, annotationId);
    }

    @Override
    protected final RESULT execute() {
        try {
            return checkedExecute();
        } catch (Exception e) {
            CAT.e(e);
            checkException(e, getActivity());
            return null;
        }
    }

    protected abstract RESULT checkedExecute() throws Exception;

    @Override
    protected final Class<RESULT> getResultClass() {
        return mResultClass;
    }

    private static void checkException(@NonNull final Exception e, @Nullable final Activity activity) {
        if (e instanceof EDAMUserException) {
            switch (((EDAMUserException) e).getErrorCode()) {
                case AUTH_EXPIRED:

                case PERMISSION_DENIED:

                    break;
            }
        }
    }
}
