package com.jaaaelu.gzw.neteasy.privatebook.fragments.dialog.everNote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.evernote.client.android.helper.Cat;
import com.jaaaelu.gzw.neteasy.evernote.task.CreateNewNoteTask;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.EverNoteActivity;

import net.vrallev.android.task.Task;
import net.vrallev.android.task.TaskExecutor;
import net.vrallev.android.task.TaskResult;

/**
 * 直接从印象笔记复制过来的
 *
 * @author rwondratschek
 */
public class CreateNoteDialogFragment extends DialogFragment {

    public static final int REQ_SELECT_IMAGE = 100;

    public static final String TAG = "CreateNoteDialogFragment";

    private static final Cat CAT = new Cat(TAG);

    private static final String KEY_IMAGE_DATA = "KEY_IMAGE_DATA";
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTO = 1;

    private CreateNewNoteTask.ImageData mImageData;

    private Book mCurrBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mImageData = savedInstanceState.getParcelable(KEY_IMAGE_DATA);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_note, null);
        final TextInputLayout titleView = (TextInputLayout) view.findViewById(R.id.textInputLayout_title);
        final TextInputLayout contentView = (TextInputLayout) view.findViewById(R.id.textInputLayout_content);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (getActivity() instanceof EverNoteActivity) {
                            ((EverNoteActivity) getActivity()).createNewNote(titleView.getEditText().getText().toString(),
                                    contentView.getEditText().getText().toString(), mImageData);
                        } else {
                            throw new IllegalStateException();
                        }
                        break;
                }
            }
        };

        if (mCurrBook != null) {
            titleView.getEditText().setText(mCurrBook.getTitle());
            contentView.getEditText().setText("《" + mCurrBook.getTitle() + "》的 ISNB 号为 -> " + mCurrBook.getIsbn13() + "，\n"
                    + "可以在豆瓣看到它的详细信息，" + "<a href='" + mCurrBook.getAlt() + "'>《" + mCurrBook.getTitle() + "》</a>");
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle("创建行笔记")
                .setView(view)
                .setPositiveButton("保存", onClickListener)
                .setNegativeButton("取消", onClickListener)
                .setNeutralButton("添加照片", onClickListener)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            Button button = alertDialog.getButton(Dialog.BUTTON_NEUTRAL);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        takePhoto();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_PHOTO);
                    }
                }
            });
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_SELECT_IMAGE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageData != null) {
            outState.putParcelable(KEY_IMAGE_DATA, mImageData);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    TaskExecutor.getInstance().execute(new QueryImageTask(data, getActivity()), this);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @TaskResult
    public void onImageData(CreateNewNoteTask.ImageData imageData) {
        mImageData = imageData;
    }

    public CreateNoteDialogFragment setBookInfo(Book currBook) {
        mCurrBook = currBook;
        return this;
    }

    private static final class QueryImageTask extends Task<CreateNewNoteTask.ImageData> {

        private static final String[] QUERY_COLUMNS = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        private final Intent mIntent;
        private final Context mContext;

        private QueryImageTask(Intent intent, Context context) {
            mIntent = intent;
            mContext = context;
        }

        @Override
        protected CreateNewNoteTask.ImageData execute() {
            Uri selectedImage = mIntent.getData();

            Cursor cursor = null;

            try {
                cursor = mContext.getContentResolver().query(selectedImage, QUERY_COLUMNS, null, null, null);
                if (cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndex(QUERY_COLUMNS[1]));
                    String fileName = cursor.getString(cursor.getColumnIndex(QUERY_COLUMNS[3]));
                    String mimeType = cursor.getString(cursor.getColumnIndex(QUERY_COLUMNS[2]));
                    return new CreateNewNoteTask.ImageData(path, fileName, mimeType);
                }

            } catch (Exception e) {
                CAT.e(e);

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return null;
        }
    }

    /**
     * 权限回调
     *
     * @param requestCode  请求码
     * @param permissions  权限列表
     * @param grantResults 结果列表
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHOTO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    takePhoto();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "您拒绝了查找本地图片的权限，暂时无法添加图片...", Toast.LENGTH_LONG).show();
                }

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
