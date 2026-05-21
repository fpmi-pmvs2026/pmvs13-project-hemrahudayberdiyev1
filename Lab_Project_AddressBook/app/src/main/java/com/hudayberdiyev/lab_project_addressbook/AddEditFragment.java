package com.hudayberdiyev.lab_project_addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACT_LOADER = 0;
    private AddEditFragmentListener listener;
    private Uri contactUri;
    private boolean addingNewContact = true;

    private EditText nameEditText, phoneEditText, emailEditText;
    private EditText streetEditText, cityEditText, stateEditText, zipEditText;
    private Button saveButton;

    public interface AddEditFragmentListener {
        void onAddEditCompleted(Uri contactUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEditFragmentListener) {
            listener = (AddEditFragmentListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        nameEditText = view.findViewById(R.id.editTextName);
        phoneEditText = view.findViewById(R.id.editTextPhone);
        emailEditText = view.findViewById(R.id.editTextEmail);
        streetEditText = view.findViewById(R.id.editTextStreet);
        cityEditText = view.findViewById(R.id.editTextCity);
        stateEditText = view.findViewById(R.id.editTextState);
        zipEditText = view.findViewById(R.id.editTextZip);
        saveButton = view.findViewById(R.id.buttonSave);

        Bundle arguments = getArguments();
        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable("contact_uri");
        }

        saveButton.setOnClickListener(v -> saveContact());

        if (contactUri != null) {
            LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        }

        return view;
    }

    private void saveContact() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseDescription.Contact.COLUMN_NAME, name);
        values.put(DatabaseDescription.Contact.COLUMN_PHONE, phoneEditText.getText().toString().trim());
        values.put(DatabaseDescription.Contact.COLUMN_EMAIL, emailEditText.getText().toString().trim());
        values.put(DatabaseDescription.Contact.COLUMN_STREET, streetEditText.getText().toString().trim());
        values.put(DatabaseDescription.Contact.COLUMN_CITY, cityEditText.getText().toString().trim());
        values.put(DatabaseDescription.Contact.COLUMN_STATE, stateEditText.getText().toString().trim());
        values.put(DatabaseDescription.Contact.COLUMN_ZIP, zipEditText.getText().toString().trim());

        if (addingNewContact) {
            Uri newUri = requireActivity().getContentResolver().insert(
                    DatabaseDescription.Contact.CONTENT_URI, values);
            if (newUri != null) {
                Toast.makeText(getActivity(), "Contact saved", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onAddEditCompleted(newUri);
                }
                clearFields();
            } else {
                Toast.makeText(getActivity(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        } else {
            int updated = requireActivity().getContentResolver().update(contactUri, values, null, null);
            if (updated > 0) {
                Toast.makeText(getActivity(), "Contact updated", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onAddEditCompleted(contactUri);
                }
            } else {
                Toast.makeText(getActivity(), "Error updating contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearFields() {
        nameEditText.setText("");
        phoneEditText.setText("");
        emailEditText.setText("");
        streetEditText.setText("");
        cityEditText.setText("");
        stateEditText.setText("");
        zipEditText.setText("");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(requireActivity(), contactUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            nameEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME)));
            phoneEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_PHONE)));
            emailEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_EMAIL)));
            streetEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STREET)));
            cityEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_CITY)));
            stateEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STATE)));
            zipEditText.setText(data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_ZIP)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}