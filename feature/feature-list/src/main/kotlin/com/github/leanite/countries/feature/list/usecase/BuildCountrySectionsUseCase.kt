package com.github.leanite.countries.feature.list.usecase

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.feature.list.extension.toSectionLetter
import com.github.leanite.countries.feature.list.ui.model.CountryLetterIndex
import com.github.leanite.countries.feature.list.ui.model.CountrySection
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import javax.inject.Inject

class BuildCountrySectionsUseCase @Inject constructor(){
    operator fun invoke(countries: List<Country>): CountrySectionList {
        val grouped: Map<Char,List<Country>> = countries.groupBy { country ->
            country.name.orEmpty().toSectionLetter()
        }

        val letters: List<Char> = ('A'..'Z').filter { grouped.containsKey(it) } +
                listOf('#').filter { grouped.containsKey('#') }

        val sections: List<CountrySection> = letters.map { letter ->
            CountrySection(
                letter = letter,
                countries = grouped[letter].orEmpty()
            )
        }

        var listItemPosition = 0
        val indexes: List<CountryLetterIndex> = sections.map { section ->
            val item = CountryLetterIndex(
                letter = section.letter,
                firstItemPosition = listItemPosition
            )
            listItemPosition += 1 + section.countries.size // header + itens da seção
            item
        }

        return CountrySectionList(
            sections = sections,
            index = indexes
        )
    }
}
