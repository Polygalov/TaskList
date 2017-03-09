package ua.com.adr.android.tasklist.activities;

import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.enums.PriorityType;
import ua.com.adr.android.tasklist.objects.AppContext;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
		todoDocument.setName(getDocumentName());

		if (actionType == AppContext.ACTION_UPDATE) {

			// если изменился текст, тогда обновить дату сохранения
			// если документ старый и текст не изменился
			if (!txtTodoDetails.getText().toString().trim()
					.equals(todoDocument.getContent())) {
				todoDocument.setContent(txtTodoDetails.getText().toString()
						.trim());
				todoDocument.setCreateDate(new Date());
			}

			// если приоритет изменился
			if (currentPriorityType != todoDocument.getPriorityType()) {
				todoDocument.setPriorityType(currentPriorityType);
				todoDocument.setCreateDate(new Date());
			}

		} else if (actionType == AppContext.ACTION_NEW_TASK) {
			todoDocument.setCreateDate(new Date());
			todoDocument.setContent(txtTodoDetails.getText().toString().trim());
			todoDocument.setPriorityType(currentPriorityType);
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
		if (actionType == AppContext.ACTION_UPDATE) {
			listDocuments.remove(docIndex);
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
				currentPriorityType = PriorityType.values()[Integer.valueOf(item.getTitleCondensed().toString())];

				return true;
			}

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

}
