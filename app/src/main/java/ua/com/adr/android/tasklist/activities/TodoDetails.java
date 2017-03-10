package ua.com.adr.android.tasklist.activities;

import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.enums.PriorityType;
import ua.com.adr.android.tasklist.objects.AppContext;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import android.content.SharedPreferences.Editor;


public class TodoDetails extends Activity {

	public static final int RESULT_SAVE = 100;
	public static final int RESULT_DELETE = 101;

	private static final int NAME_LENGTH = 20;

	private EditText txtTodoDetails;
	private TodoDocument todoDocument;

	private ArrayList<TodoDocument> listDocuments;

	private int actionType;
	private int docIndex;

	private MenuItem menuPriority;
	private PriorityType currentPriorityType;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_details);

		txtTodoDetails = (EditText) findViewById(R.id.txtTodoDetails);

		listDocuments = ((AppContext) getApplicationContext())
				.getListDocuments();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		actionType = getIntent().getExtras().getInt(AppContext.ACTION_TYPE);

		prepareDocument(actionType);
	}

	private void prepareDocument(int actionType) {
		switch (actionType) {
			case AppContext.ACTION_NEW_TASK:
				todoDocument = new TodoDocument();
				break;

			case AppContext.ACTION_UPDATE:
				docIndex = getIntent().getExtras().getInt(AppContext.DOC_INDEX);
				todoDocument = listDocuments.get(docIndex);
				txtTodoDetails.setText(todoDocument.getContent());
				break;

			default:
				break;
		}

		currentPriorityType = todoDocument.getPriorityType();
	}

	private void saveDocument() {

		if (actionType == AppContext.ACTION_UPDATE) {

			boolean edited = false;

			SharedPreferences sharedPref = getSharedPreferences(
					String.valueOf(todoDocument.getCreateDate().getTime()),
					Context.MODE_PRIVATE);

			Editor editor = sharedPref.edit();

			// если документ старый и текст изменился
			if (!txtTodoDetails.getText().toString().trim().equals(todoDocument.getContent())) {

				todoDocument.setName(getDocumentName());
				todoDocument.setContent(txtTodoDetails.getText().toString().trim());
				editor.putString(AppContext.FIELD_CONTENT,todoDocument.getContent());
				edited = true;
			}

			// если приоритет изменился
			if (currentPriorityType != todoDocument.getPriorityType()) {
				todoDocument.setPriorityType(currentPriorityType);
				editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument.getPriorityType().getIndex());
				edited = true;
			}

			if (edited) {
				String path = ((AppContext) getApplicationContext()).getPrefsDir();
				File file = new File(path, todoDocument.getCreateDate().getTime()+".xml");

				todoDocument.setCreateDate(new Date());
				editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
				editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument.getCreateDate().getTime());
				editor.commit();

				file.renameTo(new File(path, todoDocument.getCreateDate().getTime()+".xml"));

			}

		} else if (actionType == AppContext.ACTION_NEW_TASK) {
			todoDocument.setName(getDocumentName());
			todoDocument.setCreateDate(new Date());
			todoDocument.setContent(txtTodoDetails.getText().toString().trim());
			todoDocument.setPriorityType(currentPriorityType);

			SharedPreferences sharedPref = getSharedPreferences(
					String.valueOf(todoDocument.getCreateDate().getTime()),
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(AppContext.FIELD_CONTENT,
					todoDocument.getContent());
			editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
			editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument
					.getCreateDate().getTime());
			editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument
					.getPriorityType().getIndex());

			editor.commit();

			listDocuments.add(todoDocument);

		}

		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_details, menu);

		menuPriority = menu.findItem(R.id.menu_priority);

		MenuItem menuItem = menuPriority.getSubMenu().getItem(
				todoDocument.getPriorityType().getIndex());
		menuItem.setChecked(true);

		return true;
	}

	private String getDocumentName() {
		StringBuilder sb = new StringBuilder(txtTodoDetails.getText());

		if (sb.length() > NAME_LENGTH) {
			sb.delete(NAME_LENGTH, sb.length()).append("...");
		}

		String tmpName = sb.toString().trim().split("\n")[0];

		String name = (tmpName.length() > 0) ? tmpName : getResources()
				.getString(R.string.new_document);

		return name;
	}

	@SuppressLint("NewApi")
	private void deleteDocument(TodoDocument todoDocument) {

		// если открыт только что созданный документ - ничего не делаем, т.к. ничего и не сохраняли, чтобы удалять
		// если открыт ранее созданный документ, тогда удаляем его
		if (actionType == AppContext.ACTION_UPDATE) {
			Intent intent = new Intent(AppContext.RECEIVER_DELETE_DOCUMENT);
			intent.putExtra(AppContext.DOC_INDEX, todoDocument.getNumber());
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}

		finish();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {

				if (txtTodoDetails.getText().toString().trim().length() == 0) {
					finish();
				} else {
					saveDocument();
				}

				return true;
			}

			case R.id.save: {

				saveDocument();

				return true;
			}

			case R.id.delete: {

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.confirm_delete);

				builder.setPositiveButton(R.string.delete,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteDocument(todoDocument);
							}
						});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				AlertDialog dialog = builder.create();
				dialog.show();

				return true;
			}

			case R.id.menu_priority_low:
			case R.id.menu_priority_middle:
			case R.id.menu_priority_high: {
				item.setChecked(true);
				currentPriorityType = PriorityType.values()[Integer.valueOf(item
						.getTitleCondensed().toString())];

				return true;
			}

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

}

