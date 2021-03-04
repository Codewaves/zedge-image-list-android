package com.example.zedgeimagelist

import com.example.zedgeimagelist.api.ImageService
import com.example.zedgeimagelist.model.Image
import com.example.zedgeimagelist.model.ImagePage

class MockImageService : ImageService {
    override suspend fun getImagePage(page: Int): ImagePage {
        return ImagePage(
            arrayListOf(
                Image(
                    0,
                    6068966,
                    "heart, love, ornament",
                    "https://cdn.pixabay.com/photo/2021/03/04/17/51/heart-6068966_150.jpg",
                    "https://pixabay.com/get/g7c8ae4f115956ea16e8b6f6d4becfb180560ce49639f0532be6ffe56eae155bbf2c20d6ad70030d10c9fd1864ff1db4a0976e67b169db1a2474d2cce4bf692a1_640.jpg",
                    "https://pixabay.com/get/g302e33b3942ec99104ee99a3be0b3fe9ba98c8c99d4454cce8999aeecfdbffdee503624b060dd84496d1a728c8de5ec3945529cd8c422c9bbe215c288f96580f_1280.jpg",
                    0,
                    false)
            )
        )
    }

}