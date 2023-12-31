package cm.abimmobiledev.mybudgetizer.ui.earning.adapter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

        if (earning.sticker.trim().isEmpty())
            holder.incomeStickerCard.setVisibility(View.GONE);
        else
            holder.incomeStickerCard.setVisibility(View.VISIBLE);


        holder.incomeDescription.setText(earning.getReasonOrDesc());
        holder.incomeTitle.setText(earning.getEntitled());
        holder.incomeAmount.setText(String.valueOf(earning.getAmount()));
        holder.incomeDate.setText(earning.getIncomeDateAndTime());
        holder.incomeFundsSource.setText(earning.getFundsSource());
        holder.incomeSticker.setText(earning.getSticker());

        if (earning.getEntitled()!=null && earning.getEntitled().length()>0){
            holder.logoView.setText(String.valueOf(earning.getEntitled().toUpperCase(Locale.ROOT).charAt(0)));
        }

        holder.incomeCard.setOnClickListener(v -> Toast.makeText(holder.incomeCard.getContext(), "suggestions @ abdulbasihd@gmail.com. Thanks", Toast.LENGTH_LONG).show());

        holder.deleteContent.setOnClickListener(deleteItemView -> {

            AlertDialog.Builder myEarnAlertDeletionDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  "Voulez-vous vraiment supprimer ?");
            myEarnAlertDeletionDialog.setNegativeButton("Annuler", (dialog, which) -> {
                //Ne rien faire ici
            });
            myEarnAlertDeletionDialog.setPositiveButton("Continuer", (dialog, which) -> {
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
                        myEarnDeletorDialog.setPositiveButton("Ok", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });



                    notifyItemRemoved(position);
                    notifyItemChanged(position);

                });
            });
            myEarnAlertDeletionDialog.show();


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

        final TextView incomeSticker;
        final CardView incomeStickerCard;

        final TextView logoView;

        public IncomeViewHolder(@NonNull View incomeItemView) {
            super(incomeItemView);

            incomeTitle = incomeItemView.findViewById(R.id.income_title);
            incomeAmount = incomeItemView.findViewById(R.id.amount_received);
            incomeDate = incomeItemView.findViewById(R.id.income_date);
            incomeDescription = incomeItemView.findViewById(R.id.income_desc_reason);
            incomeFundsSource = incomeItemView.findViewById(R.id.funds_source);
            incomeCard = incomeItemView.findViewById(R.id.income_card);
            deleteContent = incomeItemView.findViewById(R.id.delete_content);

            incomeSticker = incomeItemView.findViewById(R.id.sticker);
            incomeStickerCard = incomeItemView.findViewById(R.id.income_sticker);

            logoView = incomeItemView.findViewById(R.id.title_char_indicator);

        }

    }

}
