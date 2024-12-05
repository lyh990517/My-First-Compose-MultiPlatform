package compose.project.demo.di

import com.slack.circuit.foundation.Circuit
import compose.project.demo.circuit.createPresenterFactory
import compose.project.demo.circuit.createUiFactory
import compose.project.demo.domain.TestUseCase
import compose.project.demo.feature.HomeScreen
import compose.project.demo.feature.NextScreen
import compose.project.demo.feature.home.HomePresenter
import compose.project.demo.feature.home.HomeScreenUi
import compose.project.demo.feature.next.NextPresenter
import compose.project.demo.feature.next.NextScreenUi
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val circuitModule = module {
    createPresenterFactory { navigator, screen ->
        when (screen) {
            is HomeScreen -> HomePresenter(screen, navigator, get())
            is NextScreen -> NextPresenter(screen, navigator)
            else -> HomePresenter(screen as HomeScreen, navigator, get())
        }
    }

    createUiFactory { state, modifier ->
        when (state) {
            is HomeScreen.State -> HomeScreenUi(state, modifier)
            is NextScreen.State -> NextScreenUi(state, modifier)
        }
    }

    single {
        Circuit.Builder()
            .addUiFactories(getAll())
            .addPresenterFactories(getAll())
            .build()
    }
}

val useCaseModule = module {
    single { TestUseCase() }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(circuitModule)
        modules(useCaseModule)
    }
}
