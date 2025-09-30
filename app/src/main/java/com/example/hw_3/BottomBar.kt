import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hw_3.BottomNavigationItems

@Composable
fun BottomBar(
    //контроллер навигации для перехода между экранами
    navController: NavHostController,
    state: Boolean,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        BottomNavigationItems.Screen1,
        BottomNavigationItems.Screen2,
        BottomNavigationItems.Screen3
    )

    //Создает контейнер навигационной панели
    NavigationBar(
        modifier = modifier,
        containerColor = Color.LightGray,
    ) {
        //получает текущую запись в стеке навигации как состояние
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->
            //отдельный элемент навигационной панели
            NavigationBarItem(
                label = {
                    Text(text = screen.title!!)
                },
                icon = {
                    Icon(imageVector = screen.icon!!, contentDescription = "")
                },
                selected = currentRoute == screen.route,
                onClick = {
                    //переход на указанный маршрут
                    navController.navigate(screen.route) {
                        //очищает стек до стартового destination
                        popUpTo(navController.graph.findStartDestination().id) {
                            //сохраняет состояние очищаемых экранов
                            saveState = true
                        }
                        //предотвращает множественные копии одного экрана
                        launchSingleTop = true
                        //восстанавливает сохраненное состояние
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedTextColor = Color.Gray,
                    selectedTextColor = Color.Black,
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Black,
                    indicatorColor = Color.White
                ),
            )
        }
    }
}
