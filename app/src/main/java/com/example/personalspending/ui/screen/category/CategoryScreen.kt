package com.example.personalspending.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.AppBarContent
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.R
import com.example.personalspending.data.Area
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.home.HeaderButton
import com.example.personalspending.ui.screen.transaction.AddButton
import com.example.personalspending.ui.screen.transaction.CategoryItem

object CategoryDestination: NavigationDestination {
    override val route: String = "category"
    override val titleRes: Int = R.string.category
    const val userIdArg = "userId"
    const val create = "create"
    val routeWithArgs = "$route/{$userIdArg}"
    val routeWithArgsForCreate = "$route/$create/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onHome: (Int) -> Unit,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    onCreate: (Int) -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isExpanded by remember { mutableStateOf(false) }
    var valueCategory by remember { mutableIntStateOf(CategoryDestination.titleRes) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = stringResource(valueCategory),
                    onClick = { isExpanded = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            )
        }
        if (isExpanded) {
            AppBarContent(
                accounts = viewModel.categoryUiState.accounts,
                user = viewModel.categoryUiState.user,
                onClick = { isExpanded = false },
                onHome = onHome,
                onAccount = onAccount,
                onCategory = onCategory,
                onChart = onChart,
                onPay = onPay,
                onRemind = onNotify,
                onPlan = onPlan,
                onLogout = onLogout,
            )
        } else {
            CategoryContent(
                listCategory = viewModel.categoryUiState.areas,
                onCreate = { onCreate(viewModel.categoryUiState.id) }
            )
        }
    }
}

@Composable
fun CategoryContent(
    modifier: Modifier = Modifier,
    listCategory: List<Area>,
    onCreate: () -> Unit,
) {
    var isExpended by remember { mutableIntStateOf(0) }
    val listCategoryItem = listCategory.filter { it.type == isExpended }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .padding(top = 100.dp)
    ) {
        HeaderButton(
            isExpended = isExpended,
            onIncome = { isExpended = 1 },
            onSpend = {isExpended = 0 }
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            items(listCategoryItem) { item ->
                CategoryItem(item = item, areaValue = 0)
            }
            item {
                AddButton(
                    onClick = onCreate,
                    title = R.string.create,
                    backgroundColor = Color(233, 189, 12),
                    size = 60.dp,
                    colorIcon = Color.White
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CategoryPreview() {
//    CategoryScreen()
}