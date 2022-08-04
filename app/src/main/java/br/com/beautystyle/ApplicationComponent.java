package br.com.beautystyle;

import android.content.Context;

import javax.inject.Singleton;

import br.com.beautystyle.retrofit.NetworkModule;
import br.com.beautystyle.database.DatabaseModule;
import br.com.beautystyle.ui.activity.LoginActivity;
import br.com.beautystyle.ui.activity.NavigationActivity;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.activity.SplashActivity;
import br.com.beautystyle.ui.fragment.client.ClientListFragment;
import br.com.beautystyle.ui.fragment.event.EventListFragment;
import br.com.beautystyle.ui.fragment.expense.CategoryListFragment;
import br.com.beautystyle.ui.fragment.expense.ExpenseListFragment;
import br.com.beautystyle.ui.fragment.job.JobListFragment;
import br.com.beautystyle.ui.fragment.report.MonthlyReportFragment;
import br.com.beautystyle.ui.fragment.report.ReportFragment;
import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {DatabaseModule.class, NetworkModule.class, BeautyStyleModule.class})
public interface ApplicationComponent {

    void injectEventFrag(EventListFragment fragment);

    void injectNewEventAct(NewEventActivity activity);

    void injectClientListFrag(ClientListFragment fragment);

    void injectJobListFrag(JobListFragment fragment);

    void injectExpenseListFrag(ExpenseListFragment fragment);

    void injectMonthlyReportFrag(MonthlyReportFragment fragment);

    void injectNavigationAct(NavigationActivity activity);

    void injectLoginAct(LoginActivity activity);

    void injectSplashAct(SplashActivity activity);

    void injectCategoryFrag(CategoryListFragment fragment);

    void injectReportFrag(ReportFragment reportFragment);

    @Component.Factory
    interface Factory{
        ApplicationComponent create(@BindsInstance Context context);
    }
}
