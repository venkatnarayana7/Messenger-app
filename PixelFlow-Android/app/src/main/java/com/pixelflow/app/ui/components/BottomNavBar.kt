package com.pixelflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelflow.app.ui.navigation.Routes
import com.pixelflow.app.ui.theme.OnSurface
import com.pixelflow.app.ui.theme.OnSurfaceVariant
import com.pixelflow.app.ui.theme.PrimaryLight
import com.pixelflow.app.ui.theme.PrimarySecondary

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testId: String,
)

private val NavItems = listOf(
    BottomNavItem(Routes.HOME,     "Home",     Icons.Outlined.Home,       "bottom-nav-home"),
    BottomNavItem(Routes.TOOLS,    "Tools",    Icons.Outlined.Build,      "bottom-nav-tools"),
    BottomNavItem(Routes.HISTORY,  "History",  Icons.Outlined.History,    "bottom-nav-history"),
    BottomNavItem(Routes.PREMIUM,  "Premium",  Icons.Outlined.EmojiEvents,"bottom-nav-premium"),
    BottomNavItem(Routes.SETTINGS, "Settings", Icons.Outlined.Settings,   "bottom-nav-settings"),
)

@Composable
fun BottomNavBar(nav: NavHostController, currentRoute: String) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("bottom-nav-bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItems.forEach { item ->
                val selected = item.route == currentRoute ||
                    (item.route == Routes.TOOLS && currentRoute in listOf(
                        Routes.RESIZE, Routes.COMPRESS, Routes.UPSCALE, Routes.CONVERT, Routes.BATCH))
                NavItemView(item = item, selected = selected) {
                    if (item.route != currentRoute) {
                        nav.navigate(item.route) {
                            popUpTo(Routes.HOME) { inclusive = false; saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .testTag(item.testId),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (selected) Modifier
                        .width(56.dp).height(32.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                        .background(PrimaryLight)
                    else Modifier.size(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (selected) PrimarySecondary else OnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) OnSurface else OnSurfaceVariant
        )
    }
}
