package ua.com.adr.android.tasklist.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.adapters.TodoAdapter;
import ua.com.adr.android.tasklist.objects.AppContext;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import ua.com.adr.android.tasklist.objects.TodoListComparator;

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
import android.widget.EditText;
import android.widget.ListView;

public class TodoList extends Activity {

	private ListView listviewTasks;
	private MenuItem menuSort;

	private EditText txtSearch;
	private ArrayList<TodoDocument> listDocuments;
	private Intent intent;
	private TodoAdapter todoAdapter;

	private static Comparator<TodoDocument> comparator = TodoListComparator
			.getDateComparator();// по-умолчанию сортировать по дате создания
	// заметки

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		listviewTasks = (ListView) findViewById(R.id.listTasks);
		listviewTasks.setOnItemClickListener(new ListViewClickListener());
		listviewTasks.setEmptyView(findViewById(R.id.emptyView));
		// listviewTasks.setTextFilterEnabled(false);

		listDocuments = ((AppContext) getApplicationContext())
				.getListDocuments();

		txtSearch = (EditText) findViewById(R.id.txtSearch);
		txtSearch.addTextChangedListener(new TextChangeListener());

		getActionBar().setDisplayHomeAsUpEnabled(false);

		intent = new Intent(this, TodoDetails.class);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		sort();
		checkSearchActive();
		checkMenuActive();
	}

	private void checkSearchActive() {
		if (listDocuments.isEmpty()) {
			txtSearch.setEnabled(false);
		} else {
			txtSearch.setEnabled(true);
		}
	}

	private void checkMenuActive() {
		if (menuSort == null) return;
		if (listDocuments.isEmpty()) {
			menuSort.setEnabled(false);
		} else {
			menuSort.setEnabled(true);
		}
	}

	private void sort() {

		Collections.sort(listDocuments, comparator);
		updateIndexes();

		// возможны более оптимальные решения: наследование от BaseAdapter,
		// запуск в параллельном потоке
		todoAdapter = new TodoAdapter(this, listDocuments);
		listviewTasks.setAdapter(todoAdapter);

		todoAdapter.getFilter().filter(txtSearch.getText());


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);

		menuSort = menu.findItem(R.id.menu_sort);

		checkMenuActive();

		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.isChecked()) {
			return true;
		}

		switch (item.getItemId()) {
			case R.id.menu_new_task: {

				Bundle bundle = new Bundle();
				bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_NEW_TASK);

				intent.putExtras(bundle);
				startActivity(intent);

				return true;
			}

			case R.id.menu_sort_name: {
				comparator = TodoListComparator.getNameComparator();
				sort();
				item.setChecked(true);
				return true;
			}

			case R.id.menu_sort_date: {
				comparator = TodoListComparator.getDateComparator();
				sort();
				item.setChecked(true);
				return true;
			}

			case R.id.menu_sort_priority: {
				comparator = TodoListComparator.getPriorityComparator();
				sort();
				item.setChecked(true);
				return true;
			}

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateIndexes() {
		int i = 0;
		for (TodoDocument doc : listDocuments) {
			doc.setNumber(i++);
		}
	}

	public void clearSearch(View view) {
		txtSearch.setText("");
	}

	private class ListViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			Bundle bundle = new Bundle();
			bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_UPDATE);
			bundle.putInt(AppContext.DOC_INDEX, ((TodoDocument) parent
					.getAdapter().getItem(position)).getNumber());

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
			todoAdapter.getFilter().filter(s);
		}

	}

}