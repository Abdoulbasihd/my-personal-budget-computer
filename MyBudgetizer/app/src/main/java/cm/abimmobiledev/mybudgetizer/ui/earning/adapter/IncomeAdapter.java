package cm.abimmobiledev.mybudgetizer.ui.earning.adapter;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private final List<Earning> earnings;

    public IncomeAdapter(List<Earning> earnings) {
        this.earnings = earnings;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View incomeItemCardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent,false);
        return new IncomeViewHolder(incomeItemCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {

        Earning earning = earnings.get(position);

        holder.incomeDescription.setText(earning.getReasonOrDesc());
        holder.incomeTitle.setText(earning.getEntitled());
        holder.incomeAmount.setText(String.valueOf(earning.getAmount()));
        holder.incomeDate.setText(earning.getIncomeDateAndTime());
        holder.incomeFundsSource.setText(earning.getFundsSource());

        holder.incomeCard.setOnClickListener(v -> Toast.makeText(holder.incomeCard.getContext(), "suggestions @ abdulbasihd@gmail.com. Thanks", Toast.LENGTH_LONG).show());

        holder.deleteContent.setOnClickListener(deleteItemView -> {

            ProgressDialog earnDelProgress = Util.initProgressDialog(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.deleting));
            earnDelProgress.show();

            AlertDialog.Builder myEarnDeletorDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  "Revenu Supprimée. Bien vouloir actualiser");


            ExecutorService incomeDelService = Executors.newSingleThreadExecutor();

            incomeDelService.execute(() -> {

                BudgetizerAppDatabase incomeDelAppDatabase = BudgetizerAppDatabase.getInstance(holder.deleteContent.getContext());
                incomeDelAppDatabase.earningDAO().delete(earning);
                earnings.remove(earning);

                new Handler(Looper.getMainLooper()).post(() -> {
                    earnDelProgress.dismiss();
                    myEarnDeletorDialog.setPositiveButton("OK", (dialog, which) -> {
                        //nothing to do here, just close
                    }).show();
                });



                notifyItemRemoved(position);
                notifyItemChanged(position);

            });
        });
    }

    @Override
    public int getItemCount() {
        return earnings.size();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        final TextView incomeTitle;
        final TextView incomeAmount;
        final TextView incomeDate;
        final TextView incomeFundsSource;
        final TextView incomeDescription;
        final CardView incomeCard;
        final CardView deleteContent;

        public IncomeViewHolder(@NonNull View incomeItemView) {
            super(incomeItemView);

            incomeTitle = incomeItemView.findViewById(R.id.income_title);
            incomeAmount = incomeItemView.findViewById(R.id.amount_received);
            incomeDate = incomeItemView.findViewById(R.id.income_date);
            incomeDescription = incomeItemView.findViewById(R.id.income_desc_reason);
            incomeFundsSource = incomeItemView.findViewById(R.id.funds_source);
            incomeCard = incomeItemView.findViewById(R.id.income_card);
            deleteContent = incomeItemView.findViewById(R.id.delete_content);

        }

    }

}
