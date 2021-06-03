package com.openclassrooms.realestatemanager.extensions

import com.openclassrooms.realestatemanager.models.Estate
import com.openclassrooms.realestatemanager.models.EstateTypes
import com.openclassrooms.realestatemanager.models.PointsOfInterests

fun generateEstates() : List<Estate> {
    return listOf(
        Estate(id = "1",
            type = EstateTypes.FLAT,
            price = 169000,
            area = 900,
            roomsAmount = 8,
            description = "PRICED FOR AN ALL CASH DEAL!!  Peaceful and quiet, this approximately 900sf home has much to offer.  Spacious one bedroom with a separate dining alcove and a huge bedroom measuring 12x18'3. The full, windowed kitchen is original, just waiting for your creative touch.  This wonderful apartment has lots of storage with 3 large closets.  There are lovely, solid oak parquet flooring throughout.  Carnegie House is situated in the heart of midtown, steps to Central Park, Carnegie Hall, Lincoln Center, the Theater District and the Time Warner Center. The full service building is pet-friendly, has a Concierge, valet, bike room, storage, and an on-site garage in which residents receive a 25% discount. Pied-a-terre, parent's buying for children, and co-purchasing are allowed.  There is a monthly special assessment of $287.24 through 2/28/22 not included in the maintenance",
            picturesUriList = listOf(
                "https://photos.zillowstatic.com/fp/5f3b6091f47a36edac88ad461d7b5518-cc_ft_576.jpg",
                "https://photos.zillowstatic.com/fp/a1ac5668a4dcc1795e250b8f2779633f-o_a.webp"
            ),
            address = "45 Rockefeller Plaza",
            city = "Manhathan",
            postalCode = "NY 10111",
            country = "USA",
            pointOfInterest = listOf(PointsOfInterests.SCHOOL, PointsOfInterests.PARK),
            available = true,
            //saleDate = Calendar.getInstance().set(2021, 5, 20) as Calendar,
            agentName = "John McLaughlin"
        )
    )
}