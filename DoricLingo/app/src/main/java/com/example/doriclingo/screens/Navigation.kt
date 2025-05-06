package com.example.doriclingo.screens

import TestingScreen
import TrainingViewModel
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.doriclingo.R
import com.example.doriclingo.repositories.ProgressRepository
import com.example.doriclingo.room.dao.ProgressDao
import com.example.doriclingo.ui.theme.DoricLingoTheme
import kotlinx.coroutines.launch
import com.example.doriclingo.room.database.UserDatabase
import com.example.doriclingo.viewmodel.state.UserState
import com.example.doriclingo.viewmodel.FirebaseAuthenticationViewModel
import com.example.doriclingo.viewmodel.ThemeViewModel
import com.example.doriclingo.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

//singleton for the database
object DatabaseProvider {
    private var dbInstance: UserDatabase? = null

    fun getDatabase(context: Context): UserDatabase {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                "users.db"
            ).build()
        }
        return dbInstance!!
    }
}
//navigation entry point
class Navigation : ComponentActivity() {

    //uses the singleton instance to get the database
    private val db by lazy {
        DatabaseProvider.getDatabase(applicationContext)
    }

    //viewmodel factory
    private val viewModel by viewModels<UserViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(db.dao, db.progressDao) as T
                }
            }
        }
    )

    //factory for training viewmodel
    //takes DAO, user ID, repository
    class TrainingViewModelFactory(
        private val progressDao: ProgressDao,
        private val userId: String?,
        private val repository: ProgressRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TrainingViewModel::class.java)) {
                return TrainingViewModel(progressDao, userId, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            //gets the theme viewmodel, dictating dark or light mode
            val themeViewModel: ThemeViewModel = viewModel()
            //applies the theme
            DoricLingoTheme(darkTheme = themeViewModel.isDarkTheme.value) {
                val state by viewModel.state.collectAsState()
                //launches the main screen
                MainScreen(viewModel = viewModel, state = state, themeViewModel = themeViewModel)
            }
        }
    }
}

//drawer menu
@Composable
fun DrawerContent(
    onItemSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val drawerWidth = 250.dp
    //list of screens
    val items = listOf(
        Screen.Home to context.getString(R.string.home),
        Screen.Training to context.getString(R.string.training),
        Screen.Testing to context.getString(R.string.testing),
        Screen.Map to context.getString(R.string.where_are_you),
        Screen.About to context.getString(R.string.about),
        Screen.Settings to context.getString(R.string.settings)
    )

    //drawer layout
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth)
            .background(MaterialTheme.colorScheme.background)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            .padding(top = 40.dp)
            .padding(16.dp),
        //ensures the drawer content takes up the whole size of the drawer
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        //loops through each screen to add them to the drawer
        items.forEach { (screen, title) ->
            DrawerMenuItem(
                title = title,
                icon = screen.icon,
                onClick = { onItemSelected(screen.route) }
            )
        }
    }
}

//each drawer item
@Composable
fun DrawerMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        //icon for the menu
        Icon(
            //takes the defined icons in the screen object
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.onBackground // Ensure visibility
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


//main screen layout
@Composable
fun MainScreen(viewModel: UserViewModel, state: UserState, themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onItemSelected = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route)
                }
            )
        }
    ) {
        //layout
        Scaffold(
            topBar = { TopAppBar(drawerState) },
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                state = state,
                themeViewModel = themeViewModel
            )
        }
    }
}

//bottom navbar
@Composable
fun BottomNavBar(navController: NavHostController) {
    val screens = listOf(Screen.Home, Screen.Map, Screen.Testing, Screen.Training)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    //styling for navbar
    NavigationBar (
        containerColor = MaterialTheme.colorScheme.surface
    ){
        NavigationBarItem(
            icon = { Icon(Screen.Home.icon, contentDescription = null) },
            label = { Text(Screen.Home.title) },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
            onClick = { navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Account.icon, contentDescription = null) },
            label = { Text(Screen.Account.title) },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Account.route } == true,
            onClick = { navController.navigate(Screen.Account.route) }
        )
        NavigationBarItem(
            icon = { Icon(Screen.Settings.icon, contentDescription = null) },
            label = { Text(Screen.Settings.title) },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Settings.route} == true,
            onClick = { navController.navigate(Screen.Settings.route) }
        )
    }
}

//top app bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //top navbar styling
    CenterAlignedTopAppBar(
        title = {
            Text(context.getString(R.string.app_name))
                },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Filled.Menu, contentDescription = context.getString(R.string.menu))
            }
        },
        //sets colours
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

//navigation between all screens
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel,
    state: UserState,
    themeViewModel: ThemeViewModel

) {
    //gets the firebase authentication view model
    val authViewModel: FirebaseAuthenticationViewModel = viewModel()
    //checks if user is logged in
    val loggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    //checks for dark mode
    val darkTheme = themeViewModel.isDarkTheme.value
    //sets the landing screen
    //if a user is logged in, they start on the home screen
    //if not, they start on the account screen
    val landingScreen = if (loggedIn) Screen.Home.route else Screen.Account.route

    //navigates to screens
    NavHost(navController, startDestination = landingScreen, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(state = state, onEvent = { viewModel.onEvent(it) },navController = navController, darkTheme = darkTheme)
        }

        composable(Screen.Map.route)
        {
            MapScreen()
        }

        composable(Screen.Training.route)
        {
            val user = FirebaseAuth.getInstance().currentUser
            TrainingScreenWrapper(
                progressDao = DatabaseProvider.getDatabase(LocalContext.current).progressDao,
                userId = user?.uid) {
                navController.navigate(Screen.Home.route)
            }
        }
        composable(Screen.Testing.route)
        {
           TestingScreen(darkTheme = darkTheme)
        }
        composable(Screen.About.route)
        {
            AboutScreen()
        }
        composable(Screen.Settings.route)
        {
            SettingsScreen(themeViewModel = themeViewModel)
        }
        composable(Screen.Account.route)
        {
            AccountScreen(state = state, onEvent = { viewModel.onEvent(it) },navController = navController, darkTheme = darkTheme)
        }
        composable(Screen.Login.route)
        {
            LoginScreen(state = state, onEvent = { viewModel.onEvent(it) }, modifier, navController, authViewModel )
        }
        composable(Screen.Signup.route)
        {
            SignupScreen(state = state, onEvent = { viewModel.onEvent(it) }, modifier, navController)
        }
    }
}
