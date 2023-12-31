package cm.abimmobiledev.mybudgetizer.ui.expense.adapter;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View expenseElementView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        return new ExpenseViewHolder(expenseElementView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        if (expense.sticker==null || expense.sticker.trim().isEmpty())
            holder.expenseStickerCard.setVisibility(View.GONE);
        else
            holder.expenseStickerCard.setVisibility(View.VISIBLE);

        holder.expenseSticker.setText(expense.getSticker());

        holder.expenseReason.setText(expense.getReason());
        holder.expenseTitle.setText(expense.getEntitled());
        holder.expenseAmount.setText(String.valueOf(expense.getAmount()));
        holder.expenseDate.setText(expense.getDateTimeOfExpense());

        if (expense.getEntitled()!=null && expense.getEntitled().length()>0){
            holder.logoView.setText(String.valueOf(expense.getEntitled().toUpperCase(Locale.ROOT).charAt(0)));
        }

        holder.expenseCard.setOnClickListener(v -> Toast.makeText(holder.expenseCard.getContext(), "suggestions @ abdulbasihd@gmail.com. Thanks", Toast.LENGTH_LONG).show());
        
        holder.deleteContent.setOnClickListener(deleteItemView -> {

            AlertDialog.Builder myExpenseDelAlertDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), "Alerte",  "Voulez-vous vraiment supprimer cette dépense");
            myExpenseDelAlertDialog.setNegativeButton("Annuler", (dialog, which) -> {
                //Ne rien faire ici
            });
            myExpenseDelAlertDialog.setPositiveButton("Continuer", (dialog, which) -> {
                ProgressDialog expenseDelProgress = Util.initProgressDialog(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.deleting));
                expenseDelProgress.show();

                AlertDialog.Builder myExpenseDeletorDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), "État",  "Dépense Supprimée. Bien vouloir actualiser");


                ExecutorService expenseDelService = Executors.newSingleThreadExecutor();

                expenseDelService.execute(() -> {

                    BudgetizerAppDatabase expenseDelAppDatabase = BudgetizerAppDatabase.getInstance(holder.deleteContent.getContext());
                    expenseDelAppDatabase.expenseDAO().delete(expense);
                    expenses.remove(expense);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        expenseDelProgress.dismiss();
                        myExpenseDeletorDialog.setPositiveButton("OK", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });



                    notifyItemRemoved(position);
                    notifyItemChanged(position);

                });
            });
            myExpenseDelAlertDialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        final TextView expenseSticker;
        final CardView expenseStickerCard;

        final TextView expenseTitle;
        final TextView expenseAmount;
        final TextView expenseDate;
        final TextView expenseReason;
        final CardView expenseCard;
        final CardView deleteContent;

        final TextView logoView;

        public ExpenseViewHolder(@NonNull View expenseItemView) {
            super(expenseItemView);

            expenseSticker = expenseItemView.findViewById(R.id.sticker);
            expenseStickerCard = expenseItemView.findViewById(R.id.expense_sticker);

            expenseTitle = expenseItemView.findViewById(R.id.expense_title);
            expenseAmount = expenseItemView.findViewById(R.id.amount_expended);
            expenseDate = expenseItemView.findViewById(R.id.expense_date);
            expenseReason = expenseItemView.findViewById(R.id.expense_reason);

            expenseCard = expenseItemView.findViewById(R.id.expense_card);
            deleteContent = expenseItemView.findViewById(R.id.delete_content);

            logoView = expenseItemView.findViewById(R.id.title_char_indicator);

        }

    }


}
