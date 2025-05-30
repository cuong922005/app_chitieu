package com.example.personalspending.ui.model

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.personalspending.SpendingApplication
import com.example.personalspending.ui.screen.account.AccountViewModel
import com.example.personalspending.ui.screen.category.CategoryViewModel
import com.example.personalspending.ui.screen.chart.ChartViewModel
import com.example.personalspending.ui.screen.details.DetailsViewModel
import com.example.personalspending.ui.screen.home.HomeViewModel
import com.example.personalspending.ui.screen.login.LoginViewModel
import com.example.personalspending.ui.screen.login.getUserId
import com.example.personalspending.ui.screen.notify.NotifyViewModel
import com.example.personalspending.ui.screen.pay.PayViewModel
import com.example.personalspending.ui.screen.plan.PlanViewModel
import com.example.personalspending.ui.screen.transaction.TransactionViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            LoginViewModel(spendingApplication().container.spendingRepository)
        }

        initializer {
            HomeViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            TransactionViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            DetailsViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            AccountViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            CategoryViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            ChartViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            PayViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            NotifyViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }

        initializer {
            PlanViewModel(
                this.createSavedStateHandle(),
                spendingApplication().container.spendingRepository
            )
        }
    }
}

fun CreationExtras.spendingApplication(): SpendingApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SpendingApplication)