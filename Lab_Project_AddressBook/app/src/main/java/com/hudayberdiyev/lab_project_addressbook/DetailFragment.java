package com.hudayberdiyev.lab_project_addressbook;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACT_LOADER = 0;
    private DetailFragmentListener listener;
    private Uri contactUri;

    private TextView nameTextView, phoneTextView, emailTextView;
    private TextView streetTextView, cityTextView, stateTextView, zipTextView;

    public interface DetailFragmentListener {
        void onContactDeleted();
        void onEditContact(Uri contactUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DetailFragmentListener) {
            listener = (DetailFragmentListener) context;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            if (listener != null && contactUri != null) {
                listener.onEditContact(contactUri);
            }
            return true;
        } else if (id == R.id.action_delete) {
            deleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        streetTextView = view.findViewById(R.id.streetTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        stateTextView = view.findViewById(R.id.stateTextView);
        zipTextView = view.findViewById(R.id.zipTextView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            contactUri = arguments.getParcelable("contact_uri");
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (contactUri != null) {
            LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        }
    }

    private void deleteContact() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Are You Sure?")
                .setMessage("This will permanently delete the contact")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (contactUri != null) {
                        requireActivity().getContentResolver().delete(contactUri, null, null);
                        Toast.makeText(getActivity(), "Contact deleted", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onContactDeleted();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(requireActivity(), contactUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String name = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME));
            String phone = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_PHONE));
            String email = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_EMAIL));
            String street = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STREET));
            String city = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_CITY));
            String state = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STATE));
            String zip = data.getString(data.getColumnIndex(DatabaseDescription.Contact.COLUMN_ZIP));

            nameTextView.setText("Name: " + (name != null ? name : ""));
            phoneTextView.setText("Phone: " + (phone != null ? phone : ""));
            emailTextView.setText("Email: " + (email != null ? email : ""));
            streetTextView.setText("Street: " + (street != null ? street : ""));
            cityTextView.setText("City: " + (city != null ? city : ""));
            stateTextView.setText("State: " + (state != null ? state : ""));
            zipTextView.setText("Zip: " + (zip != null ? zip : ""));
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