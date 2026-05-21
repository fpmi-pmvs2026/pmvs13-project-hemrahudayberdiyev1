package com.hudayberdiyev.lab_project_addressbook;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements
        ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    private ContactsFragment contactsFragment;
    private FloatingActionButton fab;
    private boolean isPhone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        // Проверка: телефон или планшет
        isPhone = (findViewById(R.id.fragmentContainer) != null);

        if (isPhone) {
            // Телефон
            if (savedInstanceState == null) {
                contactsFragment = new ContactsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainer, contactsFragment)
                        .commit();
            } else {
                contactsFragment = (ContactsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainer);
            }

            // FAB только на главном экране
            fab.setOnClickListener(v -> onAddContact());
            fab.show();

        } else {
            // Планшет
            contactsFragment = (ContactsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.contactsFragment);
            fab.setVisibility(View.GONE);
        }
    }

    // Метод для добавления нового контакта
    public void onAddContact() {
        if (isPhone) {
            displayAddEditFragment(R.id.fragmentContainer, null);
            fab.hide(); // Скрываем FAB
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, null);
        }
    }

    @Override
    public void onContactSelected(Uri contactUri) {
        if (isPhone) {
            displayContact(contactUri, R.id.fragmentContainer);
            fab.hide(); // Скрываем FAB
        } else {
            getSupportFragmentManager().popBackStack();
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onEditContact(long id) {
        Uri contactUri = DatabaseDescription.Contact.buildContactUri(id);
        if (isPhone) {
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
            fab.hide(); // Скрываем FAB
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, contactUri);
        }
    }

    @Override
    public void onDeleteContact(long id) {
        Uri contactUri = DatabaseDescription.Contact.buildContactUri(id);
        getContentResolver().delete(contactUri, null, null);
        if (contactsFragment != null) {
            contactsFragment.updateContactList();
        }
    }

    private void displayContact(Uri contactUri, int viewId) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("contact_uri", contactUri);
        detailFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(viewId, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void displayAddEditFragment(int viewId, Uri contactUri) {
        AddEditFragment addEditFragment = new AddEditFragment();
        if (contactUri != null) {
            Bundle args = new Bundle();
            args.putParcelable("contact_uri", contactUri);
            addEditFragment.setArguments(args);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(viewId, addEditFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onContactDeleted() {
        getSupportFragmentManager().popBackStack();
        if (contactsFragment != null) {
            contactsFragment.updateContactList();
        }
        fab.show(); // Показываем FAB при возврате на главный экран
    }

    @Override
    public void onEditContact(Uri contactUri) {
        if (isPhone) {
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
            fab.hide(); // Скрываем FAB
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, contactUri);
        }
    }

    @Override
    public void onAddEditCompleted(Uri contactUri) {
        getSupportFragmentManager().popBackStack();
        if (contactsFragment != null) {
            contactsFragment.updateContactList();
        }

        fab.show(); // Показываем FAB при возврате на главный экран

        if (!isPhone) {
            getSupportFragmentManager().popBackStack();
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            fab.show(); // Показываем FAB при возврате на главный экран
        } else {
            super.onBackPressed();
        }
    }
}