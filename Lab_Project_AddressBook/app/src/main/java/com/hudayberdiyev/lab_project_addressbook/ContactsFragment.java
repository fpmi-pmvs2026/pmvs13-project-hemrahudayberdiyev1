package com.hudayberdiyev.lab_project_addressbook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_LOADER = 0;
    private ContactsFragmentListener listener;
    private ContactsAdapter adapter;
    private RecyclerView recyclerView;

    public interface ContactsFragmentListener {
        void onContactSelected(Uri contactUri);
        void onEditContact(long id);
        void onDeleteContact(long id);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ContactsFragmentListener) {
            listener = (ContactsFragmentListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ContactsAdapter(new ContactsAdapter.OnContactActionListener() {
            @Override
            public void onEdit(long id) {
                if (listener != null) {
                    listener.onEditContact(id);
                }
            }

            @Override
            public void onDelete(long id) {
                if (listener != null) {
                    listener.onDeleteContact(id);
                }
            }

            @Override
            public void onView(long id) {
                Uri contactUri = DatabaseDescription.Contact.buildContactUri(id);
                if (listener != null) {
                    listener.onContactSelected(contactUri);
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDivider(recyclerView));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(CONTACTS_LOADER, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateContactList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(requireActivity(),
                DatabaseDescription.Contact.CONTENT_URI,
                null, null, null,
                DatabaseDescription.Contact.COLUMN_NAME + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (adapter != null) {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }
}