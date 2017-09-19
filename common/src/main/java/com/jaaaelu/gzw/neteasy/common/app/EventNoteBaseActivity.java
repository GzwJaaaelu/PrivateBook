package com.jaaaelu.gzw.neteasy.common.app;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;
import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.evernote.task.CreateNewNoteTask;
import com.jaaaelu.gzw.neteasy.evernote.task.FindNotesTask;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gzw on 2017/9/19 0019.
 */

public abstract class EventNoteBaseActivity extends BaseActivity {
    protected static final String QUERY_NOTE_BOOK_ID = "A";
    protected static final int MAX_NOTES = 100;
    protected List<NoteRef> mNoteRefList = new ArrayList<>();
    protected LinkedNotebook mLinkedNotebook;
    protected Notebook mNotebook;

    /**
     * 查询印象笔记的 NoteBook
     */
    protected void queryEverNoteBook() {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
            @Override
            public void onSuccess(List<Notebook> result) {
                for (Notebook notebook : result) {
                    if ("私人藏书".equals(notebook.getName())) {
                        mNotebook = notebook;
                        doSomethingWhenGetNoteBook(mNotebook);
                    }
                }
            }

            @Override
            public void onException(Exception exception) {
                onQueryNoteBookException();
            }
        });
    }

    protected void onQueryNoteBookException() {

    }

    protected abstract void doSomethingWhenGetNoteBook(Notebook notebook);

    /**
     * 查询印象笔记的 Note（这才是真的笔记）
     */
    protected void queryEverNote() {
        new FindNotesTask(0, MAX_NOTES, mNotebook, mLinkedNotebook, "").start(this, QUERY_NOTE_BOOK_ID);
    }

    /**
     * 创建笔记
     *
     * @param title     标题
     * @param content   内容
     * @param imageData 图片数据
     */
    public void createNewNote(String title, String content, CreateNewNoteTask.ImageData imageData) {
        new CreateNewNoteTask(title, content, imageData, mNotebook, mLinkedNotebook).start(this);
    }
}
