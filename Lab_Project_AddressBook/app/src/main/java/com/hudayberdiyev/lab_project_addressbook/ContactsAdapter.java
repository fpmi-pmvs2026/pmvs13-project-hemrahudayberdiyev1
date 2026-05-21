package com.hudayberdiyev.lab_project_addressbook;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Cursor cursor;
    private final OnContactActionListener actionListener;

    public interface OnContactActionListener {
        void onEdit(long id);
        void onDelete(long id);
        void onView(long id);
    }

    public ContactsAdapter(OnContactActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewName;
        public final ImageButton buttonEdit;
        public final ImageButton buttonDelete;
        private long contactId;
        private OnContactActionListener actionListener;

        public ViewHolder(@NonNull View itemView, OnContactActionListener actionListener) {
            super(itemView);
            this.actionListener = actionListener;
            textViewName = itemView.findViewById(R.id.textViewName);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            // Устанавливаем яркий стиль для текста
            textViewName.setTextColor(0xFF000000); // Черный цвет
            textViewName.setTextSize(18);

            textViewName.setOnClickListener(v -> {
                if (contactId != 0 && this.actionListener != null) {
                    this.actionListener.onView(contactId);
                }
            });

            buttonEdit.setOnClickListener(v -> {
                if (contactId != 0 && this.actionListener != null) {
                    this.actionListener.onEdit(contactId);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                if (contactId != 0 && this.actionListener != null) {
                    this.actionListener.onDelete(contactId);
                }
            });
        }

        public void setContactId(long contactId) {
            this.contactId = contactId;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);
        return new ViewHolder(view, actionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseDescription.Contact._ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME));
            holder.setContactId(id);
            holder.textViewName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        this.cursor = newCursor;
        notifyDataSetChanged();
    }
}