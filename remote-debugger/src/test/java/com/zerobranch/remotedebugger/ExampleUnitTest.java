package com.zerobranch.remotedebugger;

import org.junit.Test;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {

//        String[] lines = new String[]{"" +
//                "CREATE TABLE android_metadata (locale TEXT)",
//                "CREATE TABLE `announcement` (`id` INTEGER NOT NULL, `date` TEXT, `message` TEXT, `link` TEXT, `createdAt` INTEGER, `updatedAt` INTEGER, `read` INTEGER NOT NULL, PRIMARY KEY(`id`))",
//                "CREATE TABLE `local_station` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `expressId` INTEGER NOT NULL, `name` TEXT NOT NULL, `hasWicket` INTEGER NOT NULL, `hasValidator` INTEGER NOT NULL, `isPassengerStation` INTEGER NOT NULL, `directionName` TEXT NOT NULL, `hasAvailableTicketTo` INTEGER NOT NULL, `aliasId` TEXT NOT NULL, `isArtificial` INTEGER, `hasSchedules` INTEGER, `tutorialType` TEXT)",
//                "CREATE TABLE `schedule_station_join_entity` (`scheduleId` INTEGER NOT NULL, `scheduleStationId` INTEGER NOT NULL, PRIMARY KEY(`scheduleId`, `scheduleStationId`), FOREIGN KEY(`scheduleId`) REFERENCES `schedule`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`scheduleStationId`) REFERENCES `schedule_station`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
//                "CREATE TABLE room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)",
//                "CREATE TABLE `schedule_station` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `arrivalTime` INTEGER, `departureTime` INTEGER, `skip` INTEGER NOT NULL, `deviation` INTEGER, `deviationIsPossible` INTEGER NOT NULL, `canceled` INTEGER NOT NULL, `stationId` INTEGER)",
//                "CREATE TABLE sqlite_sequence(name,seq)",
//
//                "CREATE TABLE `comfort_option` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `latinName` TEXT NOT NULL, PRIMARY KEY(`id`))",
//                "CREATE TABLE `direction` (`id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`))",
//                "CREATE TABLE `favorite_station` (`id` INTEGER NOT NULL, `name` TEXT, `hasWicket` INTEGER NOT NULL, `stationAliasIds` TEXT NOT NULL, PRIMARY KEY(`id`))",
//                "CREATE TABLE `favorites` (`favoriteId` INTEGER NOT NULL, `fromStationId` INTEGER NOT NULL, `toStationId` INTEGER NOT NULL, `stationFromTimes` TEXT NOT NULL, `stationToTimes` TEXT NOT NULL, `position` INTEGER NOT NULL, `isForwardDirection` INTEGER NOT NULL, PRIMARY KEY(`favoriteId`))",
//                "CREATE TABLE `news` (`id` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, `date` INTEGER, `text` TEXT NOT NULL, `source` TEXT NOT NULL, `title` TEXT NOT NULL, `url` TEXT NOT NULL, `hashtags` TEXT NOT NULL, PRIMARY KEY(`id`))",
//                "CREATE TABLE `partners_cash_back` (`id` TEXT NOT NULL, `title` TEXT, `phone` TEXT, `currentBonus` TEXT, `pushRadius` TEXT, `rating` TEXT, `likes` TEXT, `categoryId` TEXT, `deliveryRootCategory` TEXT, `categoryName` TEXT, `booking` INTEGER, `liked` INTEGER, `country` TEXT, `region` TEXT, `area` TEXT, `city` TEXT, `district` TEXT, `street` TEXT, `house` TEXT, `houseIndex` TEXT, `flat` TEXT, `index` TEXT, `building` TEXT, `longitude` TEXT, `latitude` TEXT, PRIMARY KEY(`id`))",
//                "CREATE TABLE `partners_filter` (`id` TEXT NOT NULL, `title` TEXT, PRIMARY KEY(`id`))",
//                "CREATE TABLE `schedule` (`id` INTEGER NOT NULL, `trainNumber` TEXT, `motionMode` TEXT, `exceptDates` TEXT NOT NULL, `onlyByDates` TEXT NOT NULL, `startTime` INTEGER, `finishTime` INTEGER, `canceled` INTEGER NOT NULL, `cancellationIsPossible` INTEGER NOT NULL, `routeId` INTEGER NOT NULL, `startStationId` INTEGER, `finishStationId` INTEGER, `trainCategoryId` INTEGER, PRIMARY KEY(`id`))",
//                "CREATE TABLE `station` (`id` INTEGER NOT NULL, `name` TEXT, `hasWicket` INTEGER NOT NULL, `stationAliasIds` TEXT NOT NULL, PRIMARY KEY(`id`))",
//                "CREATE TABLE `station_direction_join` (`stationId` INTEGER NOT NULL, `directionId` INTEGER NOT NULL, PRIMARY KEY(`stationId`, `directionId`), FOREIGN KEY(`stationId`) REFERENCES `station`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`directionId`) REFERENCES `direction`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
//                "CREATE TABLE `tickets` (`id` INTEGER NOT NULL, `tariffId` INTEGER NOT NULL, `type` TEXT, `fromStation` INTEGER, `fromStationName` TEXT, `toStation` INTEGER, `toStationName` TEXT, `tariffPlanName` TEXT, `ticketTypeName` TEXT, `date` INTEGER, `status` TEXT, `ticketNumber` INTEGER, `barcode` TEXT, `price` REAL, `orderId` INTEGER NOT NULL, `orderDate` INTEGER, `validDate` INTEGER, `categoryId` INTEGER, `rzdTrainCategoryId` INTEGER, `policyPdfUrl` TEXT, PRIMARY KEY(`id`))",
//                "CREATE TABLE `train_category` (`id` INTEGER NOT NULL, `uiName` TEXT NOT NULL, `latinName` TEXT NOT NULL, `localUpdatedAt` INTEGER, `isSelected` INTEGER NOT NULL, PRIMARY KEY(`id`))",
//                "CREATE TABLE `transaction_cash_back` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `place` TEXT, `dateOperation` INTEGER, `amountEarn` TEXT, `amountSpend` TEXT, `type` TEXT, `state` TEXT, `amount` TEXT, `extraAmount` TEXT)",
//                "CREATE TABLE `train_category_comfort_option_join` (`trainCategoryId` INTEGER NOT NULL, `comfortOptionId` INTEGER NOT NULL, PRIMARY KEY(`trainCategoryId`, `comfortOptionId`), FOREIGN KEY(`trainCategoryId`) REFERENCES `train_category`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`comfortOptionId`) REFERENCES `comfort_option`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
//                "CREATE TABLE `travel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cost` INTEGER NOT NULL, `trainNumber` TEXT, `startTime` INTEGER, `finishTime` INTEGER, `startStationId` INTEGER NOT NULL, `startStationName` TEXT, `finishStationId` INTEGER NOT NULL, `finishStationName` TEXT, `departureStationId` INTEGER NOT NULL, `departureStationName` TEXT, `arrivalStationId` INTEGER NOT NULL, `arrivalStationName` TEXT, `departureTime` INTEGER, `arrivalTime` INTEGER, `scheduleId` INTEGER NOT NULL, `onlyByDates` TEXT NOT NULL, `exceptDates` TEXT NOT NULL, `motionMode` TEXT, `trainCategoryId` INTEGER NOT NULL, `deviation` INTEGER NOT NULL, `deviationIsPossible` INTEGER NOT NULL, `canceled` INTEGER NOT NULL, `rzdTrainCategoryId` INTEGER NOT NULL, `departureStationHasWicket` INTEGER NOT NULL, `arrivalStationHasWicket` INTEGER NOT NULL, `tariffId` INTEGER NOT NULL, `platformAndWay` TEXT, `cancellationIsPossible` INTEGER NOT NULL, `routeId` INTEGER NOT NULL, `defaultDirection` INTEGER NOT NULL)"
//        };

//        for (String line : lines) {
//            List<Table.Header> header = DatabaseManager.getHeaders(line);
//            System.out.println(header);
//        }
//        List<Table.Header> header = DatabaseManager.getHeaders("CREATE TABLE android_metadata (locale TEXT)");

//        assertEquals(header.get(0).name, "locale");
//        assertEquals(header.get(0).type, "text");
//        fail();

    }
}