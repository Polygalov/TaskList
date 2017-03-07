package ua.com.adr.android.tasklist.activities;

import ua.com.adr.android.tasklist.R;
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

public class TodoDetails extends Activity {

	public static final int RESULT_SAVE = 100;
	public static final int RESULT_DELETE = 101;

	private static final int NAME_LENGTH = 20;

	private EditText txtTodoDetails;
	private TodoDocument todoDocument;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_details);
		txtTodoDetails = (EditText) findViewById(R.id.txtTodoDetails);
		todoDocument = (TodoDocument) getIntent().getSerializableExtra(
				TodoList.TODO_DOCUMENT);
		// setTitle(todoDocument.getName());
		txtTodoDetails.setText(todoDocument.getContent());

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_details, menu);
		return true;
	}

	private void saveDocument(){
		StringBuilder sb = new StringBuilder(txtTodoDetails.getText());


		if (sb.length() > NAME_LENGTH) {
			sb.delete(NAME_LENGTH, sb.length()).append("...");
		}

		String tmpName = sb.toString().trim().split("\n")[0];

		String name = (tmpName.length() > 0) ? tmpName : todoDocument
				.getName();

		todoDocument.setName(name);

		// если у документа не редатировали текст (т.е. открыли уже ранее созданный документ)
		if (todoDocument.getContent()!=null && txtTodoDetails.getText().toString().equals(todoDocument.getContent())){
			setResult(RESULT_CANCELED, getIntent());
		}else{
			todoDocument.setContent(sb.toString());
			setResult(RESULT_SAVE, getIntent());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {

				if (txtTodoDetails.getText().toString().trim().length() == 0) {
					setResult(RESULT_CANCELED);
				} else {
					saveDocument();
				}
				finish();
				return true;
			}

			case R.id.save: {

				saveDocument();

				finish();
				return true;
			}

			case R.id.delete: {

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.confirm_delete);

				builder.setPositiveButton(R.string.delete,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								setResult(RESULT_DELETE, getIntent());
								finish();

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

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

}