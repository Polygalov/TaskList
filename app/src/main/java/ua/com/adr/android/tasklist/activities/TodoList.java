package ua.com.adr.android.tasklist.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.adapters.TodoAdapter;
import ua.com.adr.android.tasklist.objects.AppContext;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TodoList extends Activity {

	private ListView listviewTasks;
	private EditText txtSearch;
	private ArrayList<TodoDocument> listDocuments;
	private Intent intent;
	private TodoAdapter arrayAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		listviewTasks = (ListView) findViewById(R.id.listTasks);
		listviewTasks.setOnItemClickListener(new ListViewClickListener());
		listviewTasks.setEmptyView(findViewById(R.id.emptyView));
		// listviewTasks.setTextFilterEnabled(false);

		listDocuments = ((AppContext) getApplicationContext()).getListDocuments();

		txtSearch = (EditText) findViewById(R.id.txtSearch);
		txtSearch.addTextChangedListener(new TextChangeListener());

		getActionBar().setDisplayHomeAsUpEnabled(false);

		intent = new Intent(this, TodoDetails.class);


	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		arrayAdapter = new TodoAdapter(this, listDocuments);
		listviewTasks.setAdapter(arrayAdapter);
		checkSearchActive();
	}


	private void checkSearchActive() {
		if (listDocuments.isEmpty()) {
			txtSearch.setEnabled(false);
		} else {
			txtSearch.setEnabled(true);
			arrayAdapter.getFilter().filter(txtSearch.getText());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_task: {

				Bundle bundle = new Bundle();
				bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_NEW_TASK);

				intent.putExtras(bundle);
				startActivity(intent);

				return true;
			}

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ListViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			Bundle bundle = new Bundle();
			bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_UPDATE);
			bundle.putInt(AppContext.DOC_INDEX, ((TodoDocument) parent.getAdapter().getItem(position)).getNumber());

			intent.putExtras(bundle);
			startActivity(intent);
		}

	}

	private class TextChangeListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
			arrayAdapter.getFilter().filter(s);
		}

	}

}