import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
	class locations {
		public final static int Deck = 0;
		public final static int Hand = 1;
		public final static int MonsterZone = 2;
		public final static int STZone = 3;
		public final static int Graveyard = 4;
		public final static int FaceupBanished = 5;
		public final static int FacedownBanished = 6;
		public final static int excavations = 7;
	}
	public static void main(String args[]) {
		// Floowandereeze
		Card prosperity = card("Pot of Prosperity", 3);
		Card duality = card("Pot of Duality", 3);
		Card advent = card("Floowandereeze and the Advent of Adventure", 3, "Floowandereeze", "flooST");
		Card map = card("Floowandereeze and the Magnificent Map", 3, "field", "Floowandereeze", "flooST");
		Card dreamingTown = card("Floowandereeze and the Dreaming Town", 3, "Floowandereeze", "flooST");
		Card unexplored = card("Floowandereeze and the Unexplored Winds", 1, "Floowandereeze", "flooST");
		Card traptrick = card("Trap Trick", 3);
		Card toccan = card("Floowandereeze & Toccan", 1, "Floowandereeze", "winged-beast", "ns",
				"Floowandereeze Monster", "level1flunder");
		Card stri = card("Floowandereeze & Stri", 2, "Floowandereeze", "winged-beast", "ns", "Floowandereeze Monster",
				"level1flunder");
		Card robina = card("Floowandereeze & Robina", 3, "Floowandereeze", "winged-beast", "ns",
				"Floowandereeze Monster", "level1flunder");
		Card eglen = card("Floowandereeze & Eglen", 3, "Floowandereeze", "winged-beast", "ns",
				"Floowandereeze Monster", "level1flunder");
		Card stormwinds = card("Barrier Statue of the Stormwinds", 1, "winged-beast", "ns");
		Card empen = card("Floowandereeze & Empen", 3, "Floowandereeze", "winged-beast", "2tribsum",
				"Floowandereeze Monster");
		Card apexavian = card("Mist Valley Apex Avian", 1, "winged-beast", "2tribsum");
		Card raiza = card("Raiza the Mega Monarch", 1, "winged-beast", "2tribsum");
		Card terraforming = card("Terraforming", 1);
		Card brick = card("blue-eyes White Dragon", 5);
		locations("Deck", "Hand", "MZ", "STZ", "GY", "Banished", "FDBanished", "excavations");
		hand(5);
		Action resetExcavations = action("reset excavations").move_all(locations.excavations, locations.Deck);
		Action NormalSummon = action("normal summon").poss(move("ns", locations.Hand, locations.MonsterZone));
		Action TributeSummon = action("tribute summon").poss(
				move(2, "level1flunder", locations.MonsterZone, locations.FaceupBanished),
				move("2tribsum", locations.Hand, locations.MonsterZone));
		Action NormalorTributeSummon = action("normal or tribute summon").poss(NormalSummon).poss(TributeSummon);
		action("gamestate normal").open().trigger(NormalorTributeSummon).hopt();
		// no cost because working with the extra deck is difficult and it doesnt matter
		// in these kind of computations anyway
		// pot of prosperity
		Action prosperityEff = action("prosperity eff").poss(move("card", locations.excavations, locations.Hand))
				.trigger(resetExcavations);
		action("prosperity activation").poss(move(prosperity, locations.Hand, locations.Graveyard))
				.draw(locations.excavations, 6).hopt().open().trigger(prosperityEff);
		// pot of duality
		Action dualityEff = action("duality eff").poss(move("card", locations.excavations, locations.Hand))
				.trigger(resetExcavations);
		action("duality activation").poss(move(duality, locations.Hand, locations.Graveyard))
				.draw(locations.excavations, 3).hopt().open().trigger(dualityEff);
		// advent of adventure
		Action AdventEff = action("advent resolution").poss(move("field", locations.Deck, locations.Hand))
				.poss(move("Floowandereeze Monster", locations.Deck, locations.Hand))
				.hopt();
		action("advent eff")
				.poss(
						move(advent, locations.Hand, locations.Graveyard),
						move("winged-beast", list(locations.Hand, locations.MonsterZone), locations.FaceupBanished))
				.open().trigger(AdventEff);
		// terraforming
		action("terraforming eff").poss(move(terraforming, locations.Hand, locations.Graveyard),
				move("field", locations.Deck, locations.Hand)).open();
		// map
		Action mapEff = action("map resolution")
				.poss(move("Floowandereeze", locations.Deck, locations.FaceupBanished))
				.hopt().trigger(NormalorTributeSummon);
		action("activate map")
				.poss(cond("level1flunder", locations.Hand), move(map, locations.Hand, locations.STZone))
				.open().trigger(mapEff);
		// dreaming town
		action("set Trap").poss(move(traptrick, locations.Hand, locations.STZone))
				.poss(move(dreamingTown, locations.Hand, locations.STZone)).open();
		// unexplored winds
		action("place winds").poss(move(unexplored, locations.Hand, locations.STZone)).open();
		action("activate winds")
				.poss(cond(unexplored, locations.STZone), move("winged-beast", locations.Hand, locations.Deck))
				.draw(locations.Hand, 1).open().hopt();
		Action winds_1 = action("winds for 1")
				.poss(cond(unexplored, locations.STZone), move("winged-beast", locations.Hand, locations.Graveyard))
				.draw(locations.Hand, 1).open()
				.hopt();
		Action winds_2 = action("winds for 2")
				.poss(cond(unexplored, locations.STZone), move(2, "winged-beast", locations.Hand, locations.Graveyard))
				.draw(locations.Hand, 2).open()
				.hopt();
		winds_1.turnoff(winds_2);
		winds_2.turnoff(winds_1);
		// toccan
		Action toccanSum = action("toccan summon")
				.poss(move("Floowandereeze", locations.FaceupBanished, locations.Hand)).trigger(NormalorTributeSummon)
				.hopt();
		MT.add(toccan, locations.Hand, locations.MonsterZone, toccanSum);
		Action toccanBan = action("toccan banished effect")
				.poss(move(toccan, locations.FaceupBanished, locations.Hand))
				.hopt();
		MT.add("winged-beast", locations.Hand, locations.MonsterZone, toccanBan);
		// stri
		Action striSum = action("stri summon")
				.poss(move("Floowandereeze", locations.FaceupBanished, locations.Hand)).trigger(NormalorTributeSummon)
				.hopt();
		MT.add(stri, locations.Hand, locations.MonsterZone, striSum);
		Action striBan = action("stri banished effect")
				.poss(move(stri, locations.FaceupBanished, locations.Hand))
				.hopt();
		MT.add("winged-beast", locations.Hand, locations.MonsterZone, striBan);
		// robina
		Action robinaSum = action("robina summon")
				.poss(move("level1flunder", locations.Deck, locations.Hand)).trigger(NormalorTributeSummon)
				.hopt();
		MT.add(robina, locations.Hand, locations.MonsterZone, robinaSum);
		Action robinaBan = action("robina banished effect")
				.poss(move(robina, locations.FaceupBanished, locations.Hand))
				.hopt();
		MT.add("winged-beast", locations.Hand, locations.MonsterZone, robinaBan);
		// eglen
		Action eglenSum = action("eglen summon")
				.poss(move("2tribsum", locations.Deck, locations.Hand)).trigger(NormalorTributeSummon)
				.hopt();
		MT.add(eglen, locations.Hand, locations.MonsterZone, eglenSum);
		Action eglenBan = action("eglen banished effect")
				.poss(move(eglen, locations.FaceupBanished, locations.Hand))
				.hopt();
		MT.add("winged-beast", locations.Hand, locations.MonsterZone, eglenBan);
		// empen
		Action empenSum = action("empen summon")
				.poss(move("flooST", locations.Deck, locations.Hand)).trigger(NormalorTributeSummon);
		MT.add(empen, locations.Hand, locations.MonsterZone, empenSum);
		
		Gov.poss(cond(map, locations.STZone), cond(dreamingTown, locations.STZone),
				cond("level1flunder", locations.Hand),
				cond(2, "level1flunder", list(locations.FaceupBanished, locations.Hand)));
		Gov.poss(cond(apexavian, locations.MonsterZone), cond(dreamingTown, locations.STZone),
				cond("level1flunder", locations.Hand));
		Gov.poss(cond(empen, locations.MonsterZone), cond(dreamingTown, locations.STZone),
				cond("level1flunder", locations.Hand));
		go("Flunder3", 100);
	}
	public static void smain(String args[])
	{
		Card junk = card("Junk Synchron", 3, 3, "synchron", "lvl 3 tuner", "dark", "warrior", "ns", "tuner");
		Card stardust = card("Stardust Synchron", 3, 4, "stardust", "synchron", "lvl 4 tuner", "light", "ns", "tuner");
		Card converter = card("Junk Converter", 3, 2, "lvl 2", "warrior", "ns", "non tuner");
		Card doppel = card("Doppel Warrior", 3, 2, "lvl 2", "dark", "warrior", "ns", "non tuner");
		Card ascator = card("Dawnwalker", 3, 5, "lvl 5", "non tuner");
		Card cyberse = card("Cyberse Synchron", 1, 1, "lvl 1 tuner", "dark", "ns", "tuner");
		Card ant = card("Fire Ant Ascator", 1, 3, "lvl 3 tuner", "ns", "tuner");
		Card trail = card("Stardust Trail", 1, 4, "stardust", "lvl 4", "light", "ns", "non tuner");
		Card caligo = card("Caligo Claw Crow", 2, 2, "lvl 2", "dark", "ns", "non tuner");
		Card rota = card("Reinforcement of the Army", 1);
		Card reborn = card("Monster Reborn", 1);
		Card illuminate = card("Stardust Illumination", 1);
		Card overtake = card("Synchro Overtake", 3);
		Card ash = card("Ash Blossom", 3, 3, "lvl 3 tuner", "ns", "tuner");
		Card veiler = card("Effect Veiler", 3, 1, "lvl 1 tuner", "light", "ns", "tuner");
		Card imperm = card("Infinite Impermanence", 3);
		Card cbtg = card("Called by the Grave", 1);
		Card midbreaker = card("Magical Mid-Breaker Field", 1);
		Card talents = card("Triple Tactics Talent", 3);

		Card speeder = card("Junk Speeder", 0, 5, "synchro", "non tuner");
		Card ruler = card("Chaos Ruler", 0, 8, "synchro", "dark dragon synchro", "non tuner", "dark");
		Card librarian = card("T.G. Hyper Librarian", 0, 5, "synchro", "non tuner", "dark");
		Card dragster = card("F.A. Dawn Dragster", 0, 7, "synchro", "non tuner", "light");
		Card herald = card("Herald of the Arc Light", 0, 4, "synchro", "non tuner", "light");
		Card accel = card("Accel Synchron", 0, 5, "tuner synchro", "tuner", "dark");
		Card shaman = card("Celestial Double Star Shaman", 0, 2, "tuner synchro", "tuner", "light");
		Card marcher = card("Martial Metal Marcher", 0, 3, "tuner synchro", "tuner");
		Card cupid = card("Cupid Pitch", 0, 4, "tuner synchro", "tuner", "light");
		Card baronne = card("Baronne de Fleur", 0, 10, "synchro", "non tuner");
		Card crystal = card("Crystal Wing Synchro Dragon", 0, 8, "synchro", "non tuner");
		Card abyss = card("Hot Red Dragon Archfiend Abyss", 0, 9, "synchro", "dark dragon synchro", "non tuner", "dark");
		
		Card token = card("Doppel Token", 0, 1, "dark");
		Card stardust3 = card("Stardust Synchron lvl 3", 0, 3, "stardust", "synchron", "lvl 4 tuner", "light", "ns", "tuner");
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Synchro processing", "Mill processing");
		hand(5);
		
		action("Normal Summon").open().hopt().poss(move("ns", 1, 2));
		
		Action rota_eff = action("ROTA eff").open().poss(move(rota, 1, 4), move("warrior", 0, 1));
		Action overtake_eff = action("Overtake eff").open().hopt().poss(move(overtake, 1, 4), move(junk, 0, 1));
		overtake_eff.poss(move(overtake, 1, 4), move(junk, 0, 2));
		Action illuminate_eff = action("Illuminate eff").open().hopt().poss(move(illuminate, 1, 4), move("stardust", 0, 4));
		Action reborn_eff = action("Reborn eff").open().poss(move(reborn, 1, 4), move("tuner", 4, 2));
		reborn_eff.poss(move(reborn, 1, 4), move("non tuner", 4, 2));
		reborn_eff.poss(move(reborn, 1, 4), move("synchro", 4, 2));
		reborn_eff.poss(move(reborn, 1, 4), move("tuner synchro", 4, 2));
		Action ascator_eff = action("Dawnwalker eff").open().hopt().poss(move(ascator, 1, 2), move("card", 1, 4), move(ant, list(0, 1), 2));
		Action converter_search = action("Converter search eff").open().hopt().poss(move(converter, 1, 4), move("tuner", 1, 4), move("synchron", 0, 1));
		Action converter_reborn = action("Converter reborn tuner").hopt().poss(cond(converter, 4), move("tuner", 4, 2));
		MT.add(converter, 2, 6, converter_reborn);
		Action junk_eff = action("Junk Synchron eff").poss(move("lvl 2", 4, 2));
		junk_eff.poss(move("lvl 1 tuner", 4, 2));
		MT.add(junk, 1, 2, junk_eff);
		
		Action trail_banish = action("Banish Stardust Trail").off().hopt().poss(move(trail, 4, 5));
		Action trail_reborn = action("Trail eff to summon self").hopt().turnon(trail_banish).poss(move(trail, list(1, 4), 2));
		Action stardust_banish = action("Banish Stardust Synchron").off().hopt().poss(move(stardust3, 4, -1), move(stardust, -1, 5));
		stardust_banish.poss(move(stardust, 4, 5));
		Action stardust_reborn = action("Stardust Synchron tribute to summon").open().hopt().trigger(trail_reborn).turnon(stardust_banish);
		stardust_reborn.poss(move("card", 2, 4), move(stardust, list(1, 4), 2));
		Action stardust_search = action("Stardust Synchron search eff").poss(move(illuminate, 0, 1));
		Action restore_stardust_lvl = action("Return Stardust Synchron level to 4").off().hopt().poss(move(stardust3, 4, -1), move(stardust, -1, 4));
		Action illuminate_banish = action("Illuminate change level to 3").open().hopt().turnon(restore_stardust_lvl).poss(move(illuminate, 4, 5), move(stardust, 2, -1), move(stardust3, -1, 2));

		MT.add(stardust, list(1,4), 2, stardust_search);
		MT.add(stardust3, list(2, 6), 4, restore_stardust_lvl);
		MT.add(trail, list(2, 6), 4, trail_banish);
		MT.add(stardust, list(2, 6), 4, stardust_banish);
		MT.add(stardust3, list(2, 6), 4, stardust_banish);
		
		Action caligo_special = action("Caligo summon from hand").hopt().poss(cond("dark", 2), move(caligo, 1, 2));
		Action doppel_special = action("Doppel summon from hand").poss(move(doppel, 1, 2));
		MT.add("card", 4, 2, doppel_special);
		Action doppel_tokens = action("Print doppel tokens").poss(cond(doppel, 4), move(2, token, -1, 2));
		MT.add(doppel, 2, 6, doppel_tokens);
		
//		Action remove_token = action("Removing doppel token").poss(move(token, 4, -1));
//		MT.add(token, list(2, 6), 4, remove_token);
		Action synchro_processing = action("Synchro processing").move_all(6, 4);
		
		Action shaman_synchro = action("Synchro Summon Shaman").hopt().trigger(synchro_processing);
		shaman_synchro.poss(move(shaman, -1, 2));
		Action shaman_mats = action("Check for Shaman materials").open().hopt().trigger(shaman_synchro).poss(2, move("non tuner", 2, 6), move("tuner", 2, 6));
		shaman_mats.poss(2, move(token, 2, -1), move("tuner", 2, 6));
		Action shaman_eff = action("Shaman summon from GY").hopt().poss(move(3, "lvl 2", 4, 2));
		shaman_eff.poss(move(2, "lvl 2", 4, 2));
		shaman_eff.poss(move("lvl 2", 4, 2));
		MT.add(shaman, -1, 2, shaman_eff);
		Action marcher_synchro = action("Synchro Summon Metal Marcher").hopt().trigger(synchro_processing);
		marcher_synchro.poss(move(marcher, -1, 2));
		Action marcher_mats = action("Check for Marcher materials").open().hopt().trigger(marcher_synchro).poss(3, move("non tuner", 2, 6), move("tuner", 2, 6));
		marcher_mats.poss(3, move(token, 2, -1), move("tuner", 2, 6));
		Action marcher_eff = action("Marcher summon from GY").hopt().poss(move("tuner", 4, 2));
		MT.add(marcher, -1, 2, marcher_eff);
		Action speeder_synchro = action("Synchro Summon Speeder").trigger(synchro_processing);
		speeder_synchro.poss(move(speeder, -1, 2));
		Action speeder_mats = action("Check for Speeder materials").open().hopt().trigger(speeder_synchro).poss(5, move("synchron", 2, 6), move("non tuner", 2, 6));
		speeder_mats.poss(5, move("synchron", 2, 6), move(token, 2, -1));
		Action speeder_eff = action("Speeder summon from deck").hopt().poss(move(stardust, 0, 2), move(junk, 0, 2), move(cyberse, 0, 2));
		speeder_eff.poss(move(stardust, 0, 2), move(cyberse, 0, 2));
		speeder_eff.poss(move(junk, 0, 2), move(cyberse, 0, 2));
		speeder_eff.poss(move(stardust, 0, 2));
		speeder_eff.poss(move(junk, 0, 2));
		speeder_eff.poss(move(cyberse, 0, 2));
		MT.add(speeder, -1, 2, speeder_eff);
		Action librarian_synchro = action("Synchro Summon Librarian").trigger(synchro_processing);
		librarian_synchro.poss(move(librarian, -1, 2));
		Action librarian_mats = action("Check for Librarian materials").open().hopt().trigger(librarian_synchro).poss(5, move("non tuner", 2, 6), move("tuner", 2, 6));
		librarian_mats.poss(5, move(token, 2, -1), move("tuner", 2, 6));
		Action librarian_draw = action("Librarian draw eff").poss(cond(librarian, 2)).draw(1, 1);
		synchro_processing.trigger(librarian_draw);
		Action ruler_synchro = action("Synchro Summon Chaos Ruler").trigger(synchro_processing);
		ruler_synchro.poss(move(ruler, -1, 2));
		Action ruler_mats = action("Check for Chaos Ruler materials").open().hopt().trigger(ruler_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
		ruler_mats.poss(8, move(token, 2, -1), move("tuner", 2, 6));
		Action ruler_mill = action("Chaos Ruler mill 5").hopt().draw(7, 5);
		MT.add(ruler, -1, 2, ruler_mill);
		Action mill_processing = action("Mill processing").poss(move("light", 7, 1)).move_all(7,4);
		mill_processing.poss(move("dark", 7, 1));
		mill_processing.poss(move("light", 7, 4));
		mill_processing.poss(move("dark", 7, 4));
		ruler_mill.trigger(mill_processing);
		Action ruler_banish = action("Banish Chaos Ruler").off().hopt().poss(move(ruler, 4, 5));
		MT.add(ruler, list(2, 6), 4, ruler_banish);
		Action ruler_reborn = action("Chaos Ruler reborn self").open().hopt().turnon(ruler_banish).poss(move(ruler, 4, 2), move("dark", 4, 5), move("light", 4, 5));
		Action crystal_synchro = action("Synchro Summon Crystal Wing").trigger(synchro_processing);
		crystal_synchro.poss(move(crystal, -1, 2));
		Action crystal_mats = action("Check for Crystal Wing materials").open().hopt().trigger(crystal_synchro).poss(8, move("synchro", 2, 6), move("tuner", 2, 6));
		Action abyss_synchro = action("Synchro Summon HRDA Abyss").trigger(synchro_processing);
		abyss_synchro.poss(move(abyss, -1, 2));
		Action abyss_mats = action("Check for Abyss mats").open().hopt().trigger(abyss_synchro).poss(9, move("dark dragon synchro", 2, 6), move("tuner", 2, 6));
		Action baronne_synchro = action("Synchro Summon Baronne de Fleur").trigger(synchro_processing);
		baronne_synchro.poss(move(baronne, -1, 2));
		Action baronne_mats = action("Check for Baronne mats").open().hopt().trigger(baronne_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
		baronne_mats.poss(10, move(token, 2, -1), move("tuner", 2, 6));
		
		Gov.poss(cond(crystal, 2), cond(abyss, 2), cond(baronne, 2));
//		Gov.poss(cond(junk, 2), cond(doppel, 2));
		go("Speeder turbo", 100);
	}
	
	
	public static void nmain(String args[])
	{
		Card moye = card("Mo Ye", 3, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card taia  = card("Tai A", 2, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card longyuan = card("Long Yuan", 3, 6, "wyrm", "swordsoul", "swordsoul monster", "effect", "non tuner");
		Card dragonsword = card("Dragonsword", 3, "swordsoul");
		Card ashuna = card("Ashuna", 3, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card vishudda = card("Vishudda", 2, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card adhara  = card("Adhara", 3, 1, "wyrm", "tenyi", "bad ns", "tuner", "effect");
		Card vessel = card("Vessel", 0);
		Card blackout = card("Blackout", 1, "swordsoul");
//		Card summit = card("Summit", 1, "swordsoul");
//		Card crossout = card("Crossout", 3);
//		Card cbtg = card("CBTG", 1, "handtrap");
		Card desires = card("Pot of Desires", 1);
		Card ash = card("Ash", 3, 3, "bad ns", "tuner", "effect", "handtrap");
		Card ogre = card("Ogre", 3, 3, "bad ns", "tuner", "psychic", "effect", "handtrap");
//		Card belle = card("Belle", 2, 3, "bad ns", "tuner", "effect", "handtrap");
		Card veiler = card("Veiler", 3, 1, "bad ns", "tuner", "effect", "handtrap");
		Card imperm = card("Imperm", 3, "handtrap");
//		Card cosmic = card("Cosmic Cyclone", 2, "handtrap");
		Card nibiru = card("Nibiru", 2, 11, "handtrap", "effect");
		Card shthana = card("Shthana", 1, "bad ns", "wyrm", "tenyi", "lvl 4", "effect");
		Card circle = card("Circle", 2);
		Card deskbot = card("Deskbot 001", 1, 1, "bad ns", "tuner", "effect");
//		Card ecclesia = card("Incredible Ecclesia", 3, 4, "good ns", "tuner", "effect");
		Card etele = card("Emergency Teleport", 3);
//		Card gamma = card("Psy-Framegear Gamma", 3, 2, "handtrap", "tuner", "effect", "psychic");
//		Card driver = card("Psy-Frame Driver", 1, 6, "psychic");
		
		Card monk = card("Monk", 0, "wyrm", "tenyi", "link", "non effect");
		Card halq = card("Halqifibrax", 0, "link", "effect");
		Card auroradon = card("Auroradon", 0, "link", "effect");
		Card chixiao = card("Chi Xiao", 0, 8, "wyrm", "swordsoul", "synchro", "effect");
		Card baronne = card("Baronne", 0, 10, "synchro", "effect");
//		Card chengying = card("Cheng Ying", 0, 10, "wyrm", "swordsoul", "synchro", "effect");
//		Card baxia = card("Baxia", 0, 8, "wyrm", "yang zing", "synchro", "effect");
//		Card berserker = card("Draco Berserker", 0, 8, "wyrm", "tenyi", "synchro", "effect");
//		Card chaofeng = card("Chaofeng", 0, 9, "wyrm", "yang zing", "synchro", "effect");
//		Card dragite = card("Dragite", 0, 8, "synchro", "effect");
		Card yazi = card("Yazi", 0, 7, "wyrm", "yang zing", "synchro", "effect");
//		Card herald = card("Herald of the Arc Light", 0, 4, "synchro", "effect");
		
		Card token = card("Swordsoul Token", 0, 4, "token", "swordsoul", "non effect", "lvl 4 tuner");
		Card dontoken = card("Auroradon Token", 0, 3, "token", "non effect", "lvl 3");
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Synchro processing", "Desires zone");
		hand(5);
		
//		Action draw_second = action("Draw for turn").open().hopt().poss(zcond("card", 2)).draw(1, 1);
		
		Action dragonsword_eff = action("Dragonsword eff").open().hopt().poss(move(dragonsword, 1, 4), move("swordsoul monster", 0, 1));
//		Action summit_eff = action("Summit eff").open().hopt().poss(move(summit, 1, 4), move("swordsoul monster", 4, 2));
		
		Action vessel_eff = action("Vessel eff").open().hopt();
		vessel_eff.poss(move(vessel, 1, 4), cond("non effect", 2), move("tenyi", 0, 4).exclude(vishudda), move("tenyi", 0, 1));
		
		Action circle_eff = action("Circle eff").open().hopt();
		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2));
		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1));
//		circle_eff.poss(cond("non effect", 2), move(circle, 4, 5), move("tenyi", 0, 1));
		
		Action blackout_eff = action("Blackout summon token").hopt().poss(move(token, -1, 2));
		MT.add(blackout, list(0, 1, 3, 4), 5, blackout_eff);
		Action blackout_set = action("Set Blackout").open().hopt().poss(move(blackout, 1, 3));
		Action desires_eff = action("Activate Desires").open().hopt().poss(move(desires, 1, 4)).draw(7, 10).draw(1,2);
		Action etele_eff = action("Activate E Tele").open().poss(cond("card", 2), move(etele, 1, 4), move(ogre, list(0, 1), 2));
		action("Normal Summon").open().hopt().poss(move("good ns", 1, 2)).poss(zcond("good ns", 1), move("bad ns", 1, 2));
		
		Action moye_negated = action("Mo Ye summon eff negated").hopt();
		
		moye_negated.poss(cond("tenyi", 1), move(circle, 1, 4), move(token, -1, 2), move(moye, 2, 4), move(shthana, 0, 1));
		moye_negated.poss(zcond("tenyi", 1), move(circle, 1, 4), cond("swordsoul", 1), move(token, -1, 2), move(moye, 2, 4), move(shthana, 0, 1));
		moye_negated.poss(cond("tenyi", 1));
		moye_negated.poss(zcond("tenyi", 1), cond("swordsoul", 1));
		MT.add(moye, list(0,1,4), 2, moye_negated);
		
		Action moye_summon = action("Mo Ye summon token").hopt().off();
		moye_summon.poss(cond("tenyi", 1), move(token, -1, 2));
		moye_summon.poss(zcond("tenyi", 1), cond("swordsoul", 1), move(token, -1, 2));
		MT.add(moye, list(0,1,4), 2, moye_summon);
		
		Action taia_negated = action("Tai A summon eff negated").open().hopt();
		taia_negated.poss(move(circle, 1, 4), cond(taia, 2), move("swordsoul", 4, 5), move(token, -1, 2), move(taia, 2, 4), move(shthana, 0, 1));
		taia_negated.poss(move(circle, 1, 4), cond(taia, 2), zcond("swordsoul", 4), move("tenyi", 4, 5), move(token, -1, 2), move(taia, 2, 4), move(shthana, 0, 1));
		taia_negated.poss(move(circle, 1, 4), cond(taia, 2), zcond("swordsoul", 4), zcond("tenyi", 4), move("yang zing", 4, 5), move(token, -1, 2), move(taia, 2, 4), move(shthana, 0, 1));
		taia_negated.poss(cond(taia, 2), move("swordsoul", 4, 5));
		taia_negated.poss(cond(taia, 2), zcond("swordsoul", 4), move("tenyi", 4, 5));
		taia_negated.poss(cond(taia, 2), zcond("swordsoul", 4), zcond("tenyi", 4), move("yang zing", 4, 5));
		
		Action taia_summon = action("Tai A summon token").open().hopt().off();
		taia_summon.poss(cond(taia, 2), move("swordsoul", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), zcond("swordsoul", 4), move("tenyi", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), zcond("swordsoul", 4), zcond("tenyi", 4), move("yang zing", 4, 5), move(token, -1, 2));
		
		Action longyuan_eff = action("Long Yuan eff in hand").open().hopt();
		longyuan_eff.poss(zcond("tenyi", 1), move("swordsoul", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff.poss(move("tenyi", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		
		moye_negated.onOff(taia_summon, taia_negated);
		taia_negated.onOff(moye_summon, moye_negated);
		
//		Action longyuan_eff_banish = action("Long Yuan eff in hand").open().hopt().off();
//		longyuan_eff_banish.poss(zcond("tenyi", 1), move("swordsoul", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
//		longyuan_eff_banish.poss(move("tenyi", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		
//		Action ecclesia_eff = action("Ecclesia summon from deck").open().hopt().poss(move(ecclesia, 2, 4), move("swordsoul monster", 0, 2).exclude(longyuan));
		
		Action deskbot_eff = action("Deskbot 001 reborn self").hopt().poss(move(deskbot, 4, 2));
		
		Action synchro_processing = action("Synchro processing").move_all(6, 4);
//		Action herald_synchro = action("Synchro Summon Herald of the Arc Light").trigger(synchro_processing);
//		herald_synchro.poss(move(herald, -1, 2));
//		Action herald_mats = action("Check for Herald mats").open().hopt().trigger(herald_synchro);
//		herald_mats.poss(4, move("tuner", 2, 4), move(dontoken, 2, -1));
		Action yazi_synchro = action("Synchro Summon Yazi").trigger(synchro_processing);
		yazi_synchro.poss(move(yazi, -1, 2));
		Action yazi_mats = action("Check for Yazi mats").open().hopt().trigger(yazi_synchro).poss(7, move("non tuner", 2, 6), move("tuner", 2, 6));
		yazi_mats.poss(7, move(token, 2, -1), move("non tuner", 2, 6));
		yazi_mats.poss(move("tuner", 2, 6), move(2, dontoken, 2, -1));
		yazi_mats.poss(7, move(token, 2, -1), move(dontoken, 2, -1));
		Action yazi_eff = action("Yazi eff summon from deck").hopt().poss(move("swordsoul monster", 0, 2));
		Action chixiao_synchro = action("Synchro Summon Chi Xiao").trigger(synchro_processing);
		chixiao_synchro.poss(move(chixiao, -1, 2));
		Action chixiao_mats = action("Check for Chi Xiao mats").open().hopt().trigger(chixiao_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
		chixiao_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
		
		Action chixiao_negated = action("Chi Xiao Negated").hopt();
		MT.add(chixiao, -1, 2, chixiao_negated);
		Action chixiao_eff = action("Chi Xiao Summon Eff").hopt().off();
		chixiao_eff.poss(move("swordsoul", 0, 1));
		chixiao_eff.poss(move(blackout, 0, 5));
		MT.add(chixiao, -1, 2, chixiao_eff);
		
		moye_negated.onOff(chixiao_eff, chixiao_negated);
		taia_negated.onOff(chixiao_eff, chixiao_negated);
		chixiao_negated.onOff(taia_summon, taia_negated);
		chixiao_negated.onOff(moye_summon, moye_negated);
		
//		Action chengying_synchro = action("Synchro Summon Cheng Ying").trigger(synchro_processing);
//		chengying_synchro.poss(move(chengying, -1, 2));
//		Action chengying_mats = action("Check for Cheng Ying mats").open().hopt().trigger(chengying_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
//		chengying_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
//		Action berserker_synchro = action("Synchro Summon Draco Berserker").trigger(synchro_processing);
//		berserker_synchro.poss(move(berserker, -1, 2));
//		Action berserker_mats = action("Check for Draco Berserker mats").open().hopt().trigger(berserker_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		berserker_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action dragite_synchro = action("Synchro Summon Dragite").trigger(synchro_processing);
//		dragite_synchro.poss(move(dragite, -1, 2));
//		Action dragite_mats = action("Check for Dragite mats").open().hopt().trigger(dragite_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		dragite_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_synchro = action("Synchro Summon Baxia").trigger(synchro_processing);
//		baxia_synchro.poss(move(baxia, -1, 2));
//		Action baxia_mats = action("Check for Baxia mats").open().hopt().trigger(baxia_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		baxia_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_reborn = action("Baxia reborn eff").open().hopt();
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("bad ns", 4, 2));
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("good ns", 4, 2));
//		Action chaofeng_synchro = action("Synchro Summon Chaofeng").trigger(synchro_processing);
//		chaofeng_synchro.poss(move(chaofeng, -1, 2));
//		Action chaofeng_mats = action("Check for Chaofeng mats").open().hopt().trigger(chaofeng_synchro).poss(9, move("yang zing", 2, 6), move("tuner", 2, 6));
		Action baronne_synchro = action("Synchro Summon Baronne de Fleur").trigger(synchro_processing);
		baronne_synchro.poss(move(baronne, -1, 2));
		Action baronne_mats = action("Check for Baronne mats").open().hopt().trigger(baronne_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
		baronne_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
		baronne_mats.poss(move(3, dontoken, 2, -1), move(deskbot, 2, 6));
		baronne_mats.poss(move(token, 2, -1), move(2, dontoken, 2, -1));
		
		Action monk_link = action("Link Summon Monk").open().hopt().poss(move("tenyi", 2, 4).exclude(monk), move(monk, -1, 2));
		Action halq_link = action("Link Summon Halqifibrax").open().hopt().poss(move("tuner", 2, 4), move("card", 2, 4).exclude("token"), move(halq, -1, 2));
		Action auroradon_link = action("Link Summon Auroradon").open().hopt().poss(move(halq, 2, 4), move(deskbot, 2, 4), move(auroradon, -1, 2));
		Action halq_negated = action("Halq eff negated").hopt();
		halq_negated.onOff(moye_summon, moye_negated);
		halq_negated.onOff(taia_summon, taia_negated);
		halq_negated.onOff(chixiao_eff, chixiao_negated);
		Action halq_eff = action("Halqifibrax eff summon from deck").hopt().off().poss(move(deskbot, list(0, 1), 2));
		Action auroradon_eff = action("Auroradon eff summon tokens").hopt().trigger(deskbot_eff).poss(move(3, dontoken, -1, 2)).turnoff(monk_link);
		MT.add(halq, -1, 2, halq_negated);
		MT.add(halq, -1, 2, halq_eff);
		MT.add(auroradon, -1, 2, auroradon_eff);
		moye_negated.onOff(halq_eff, halq_negated);
		taia_negated.onOff(halq_eff, halq_negated);
		chixiao_negated.onOff(halq_eff, halq_negated);
		baronne_synchro.onOff(moye_summon, moye_negated);
		baronne_synchro.onOff(taia_summon, taia_negated);
		baronne_synchro.onOff(halq_eff, halq_negated);
		baronne_synchro.onOff(chixiao_eff, chixiao_negated);
		
		Action auroradon_pop = action("Auroradon eff pop a card").hopt().open().trigger(yazi_eff).poss(cond(auroradon, 2), move(dontoken, 2, -1), move(yazi, 2, 4));
//		auroradon_pop.poss(move(auroradon, 2, 4), move(yazi, 2, 4));
		
		Action moye_draw = action("Mo Ye GY eff").hopt().draw(1, 1);
		MT.add(moye, 2, 6, moye_draw);
		Action taia_mill = action("Tai A GY eff").hopt();
		taia_mill.poss(move(ashuna, 0, 4)).poss(move(adhara, 0, 4));
//		taia_mill.poss(cond(summit, 1), move(moye, 0, 4));
//		taia_mill.poss(zcond(summit, 1), move(adhara, 0, 4));
//		MT.add(taia, 2, 6, taia_mill);
//		Action taia_banish = action("Tai A GY eff").hopt().off();
//		taia_banish.poss(move("wyrm", 0, 5).exclude(taia));
//		MT.add(taia, 2, 6, taia_banish);
		
		Action ashuna_summon = action("Ashuna summon from hand").open().hopt().poss(zcond("effect", 2), move(ashuna, 1, 2));
		Action vishudda_summon = action("Vishudda summon from hand").open().hopt().poss(zcond("effect", 2), move(vishudda, 1, 2));
		Action shthana_summon = action("Shthana summon from hand").open().hopt().poss(zcond("effect", 2), move(shthana, 1, 2));
		Action adhara_summon = action("Adhara summon from hand").open().hopt().poss(zcond("effect", 2), move(adhara, 1, 2));
		Action ashuna_gy = action("Ashuna eff in gy").open().hopt();
//		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna));
		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna).exclude(shthana));
		
//		ashuna_gy.turnoff(herald_synchro).turnoff(baronne_synchro).turnoff(dragite_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
		
		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
		
//		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link);
		
		Action adhara_gy = action("Adhara eff in gy").open().hopt().poss(move(adhara, 4, 5), move("wyrm", 5, 1).exclude(adhara).exclude("link").exclude("synchro"));
		
//		Action herald_floodgate = action("Herald banish on").turnoff(taia_mill).turnon(taia_banish).turnoff(longyuan_eff).turnon(longyuan_eff_banish);
//		MT.add(herald, -1, 2, herald_floodgate);
//		Action herald_unfloodgate = action("Herald banish off").turnoff(taia_banish).turnon(taia_mill).turnon(longyuan_eff).turnoff(longyuan_eff_banish);
//		MT.add(herald, 2, 4, herald_unfloodgate);
		
		Action token_lock = action("Token Lock").turnoff(monk_link).turnoff(halq_link).turnoff(auroradon_link);
		MT.add(token, -1, 2, token_lock);
		
		Action token_unlock = action("Token Unlock").turnon(monk_link).turnon(halq_link).turnon(auroradon_link);
		MT.add(token,  2, -1, token_unlock);
		
//		Gov.poss(cond("handtrap", 1), cond(chixiao, 2), cond(baronne, 2));
//		Gov.terminate(cond(1, '-', "handtrap", 1));
//		Gov.terminate(zcond("handtrap", 1));
//		Gov.poss(cond(baronne, 2), cond(chixiao, 2), cond(blackout, 3));
		Gov.poss(cond(chixiao, 2), cond(baronne, 2));
//		DeckEdit[] ch = {new DeckEdit(new Card[] {nibiru, circle}, new int[] {1, 3}), 
//				new DeckEdit(new Card[] {nibiru, circle}, new int[] {2, 2}),
//				new DeckEdit(new Card[] {vessel, circle, shthana}, new int[] {2, 0, 0}),
//				new DeckEdit(new Card[] {nibiru, vessel}, new int[] {1, 3})	
//				};
//		go("Chi Xiao + Baronne Through a Handtrap", 2000, ch);
		go("Chi Xiao + Baronne Through a Handtrap 42 Cards 2 Circle 14 Handtraps", 200);
//		go("Chi Xiao + Baronne + Blackout Through a Handtrap 48 Cards", 2000);
	}
	
	public static void ssmain(String args[])
	{
		Card moye = card("Mo Ye", 3, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card taia  = card("Tai A", 2, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card longyuan = card("Long Yuan", 3, 6, "wyrm", "swordsoul", "swordsoul monster", "effect", "non tuner");
		Card dragonsword = card("Dragonsword", 3, "swordsoul");
		Card ashuna = card("Ashuna", 3, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card vishudda = card("Vishudda", 2, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card adhara  = card("Adhara", 3, 1, "wyrm", "tenyi", "bad ns", "tuner", "effect");
		Card vessel = card("Vessel", 3);
		Card blackout = card("Blackout", 1, "swordsoul");
//		Card summit = card("Summit", 1, "swordsoul");
//		Card crossout = card("Crossout", 3);
		Card cbtg = card("CBTG", 1, "handtrap");
		Card desires = card("Pot of Desires", 1);
		Card ash = card("Ash", 3, 3, "bad ns", "tuner", "effect", "handtrap");
		Card ogre = card("Ogre", 3, 3, "bad ns", "tuner", "psychic", "effect", "handtrap");
		Card belle = card("Belle", 3, 3, "bad ns", "tuner", "effect", "handtrap");
		Card veiler = card("Veiler", 2, 1, "bad ns", "tuner", "effect", "handtrap");
		Card imperm = card("Imperm", 2, "handtrap");
//		Card cosmic = card("Cosmic Cyclone", 2, "handtrap");
		Card nibiru = card("Nibiru", 3, 11, "handtrap", "effect");
		Card shthana = card("Shthana", 1, "bad ns", "wyrm", "tenyi", "lvl 4", "effect");
		Card circle = card("Circle", 3);
		Card deskbot = card("Deskbot 001", 1, 1, "bad ns", "tuner", "effect");
		Card ecclesia = card("Incredible Ecclesia", 3, 4, "good ns", "tuner", "effect");
//		Card etele = card("Emergency Teleport", 3);
//		Card gamma = card("Psy-Framegear Gamma", 3, 2, "handtrap", "tuner", "effect", "psychic");
//		Card driver = card("Psy-Frame Driver", 1, 6, "psychic");
		
		Card monk = card("Monk", 0, "wyrm", "tenyi", "link", "non effect");
		Card halq = card("Halqifibrax", 0, "link", "effect");
		Card auroradon = card("Auroradon", 0, "link", "effect");
		Card chixiao = card("Chi Xiao", 0, 8, "wyrm", "swordsoul", "synchro", "effect");
		Card baronne = card("Baronne", 0, 10, "synchro", "effect");
//		Card chengying = card("Cheng Ying", 0, 10, "wyrm", "swordsoul", "synchro", "effect");
//		Card baxia = card("Baxia", 0, 8, "wyrm", "yang zing", "synchro", "effect");
//		Card berserker = card("Draco Berserker", 0, 8, "wyrm", "tenyi", "synchro", "effect");
//		Card chaofeng = card("Chaofeng", 0, 9, "wyrm", "yang zing", "synchro", "effect");
//		Card dragite = card("Dragite", 0, 8, "synchro", "effect");
		Card yazi = card("Yazi", 0, 7, "wyrm", "yang zing", "synchro", "effect");
//		Card herald = card("Herald of the Arc Light", 0, 4, "synchro", "effect");
		
		Card token = card("Swordsoul Token", 0, 4, "token", "swordsoul", "non effect", "lvl 4 tuner");
		Card dontoken = card("Auroradon Token", 0, 3, "token", "non effect", "lvl 3");
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Synchro processing", "Desires zone");
		hand(5);
		
//		Action draw_second = action("Draw for turn").open().hopt().poss(zcond("card", 2)).draw(1, 1);
		
		Action dragonsword_eff = action("Dragonsword eff").open().hopt().poss(move(dragonsword, 1, 4), move("swordsoul monster", 0, 1));
//		Action summit_eff = action("Summit eff").open().hopt().poss(move(summit, 1, 4), move("swordsoul monster", 4, 2));
		
		Action vessel_eff = action("Vessel eff").open().hopt();
		vessel_eff.poss(move(vessel, 1, 4), cond("non effect", 2), move("tenyi", 0, 4).exclude(vishudda), move("tenyi", 0, 1));
		
		Action circle_eff = action("Circle eff").open().hopt();
//		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2));
//		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1));
		circle_eff.poss(cond("non effect", 2), move(circle, 4, 5), move("tenyi", 0, 1));
		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2).exclude(shthana));
		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1).exclude(shthana));
		
		
		Action blackout_eff = action("Blackout summon token").hopt().poss(move(token, -1, 2));
		MT.add(blackout, list(0, 1, 3, 4), 5, blackout_eff);
		Action blackout_set = action("Set Blackout").open().hopt().poss(move(blackout, 1, 3));
		Action desires_eff = action("Activate Desires").open().hopt().poss(move(desires, 1, 4)).draw(7, 10).draw(1,2);
//		Action etele_eff = action("Activate E Tele").open().poss(cond("card", 2), move(etele, 1, 4), move(ogre, list(0, 1), 2));
		action("Normal Summon").open().hopt().poss(move("good ns", 1, 2)).poss(zcond("good ns", 1), move("bad ns", 1, 2));
		
		
		Action moye_summon = action("Mo Ye summon token").hopt();
		moye_summon.poss(cond("tenyi", 1), move(token, -1, 2));
		moye_summon.poss(zcond("tenyi", 1), cond("swordsoul", 1), move(token, -1, 2));
		MT.add(moye, list(0,1,4), 2, moye_summon);
		Action taia_summon = action("Tai A summon token").open().hopt();
		taia_summon.poss(cond(taia, 2), move("tenyi", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), zcond("tenyi", 4), move("swordsoul", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), move("yang zing", 4, 5), move(token, -1, 2));
		Action longyuan_eff = action("Long Yuan eff in hand").open().hopt();
		longyuan_eff.poss(zcond("tenyi", 1), move("swordsoul", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff.poss(move("tenyi", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		Action longyuan_eff_banish = action("Long Yuan eff in hand").open().hopt().off();
		longyuan_eff_banish.poss(zcond("tenyi", 1), move("swordsoul", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff_banish.poss(move("tenyi", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		Action ecclesia_eff = action("Ecclesia summon from deck").open().hopt().poss(move(ecclesia, 2, 4), move("swordsoul monster", 0, 2).exclude(longyuan));
		Action deskbot_eff = action("Deskbot 001 reborn self").hopt().poss(move(deskbot, 4, 2));
		
		Action synchro_processing = action("Synchro processing").move_all(6, 4);
//		Action herald_synchro = action("Synchro Summon Herald of the Arc Light").trigger(synchro_processing);
//		herald_synchro.poss(move(herald, -1, 2));
//		Action herald_mats = action("Check for Herald mats").open().hopt().trigger(herald_synchro);
//		herald_mats.poss(4, move("tuner", 2, 4), move(dontoken, 2, -1));
		Action yazi_synchro = action("Synchro Summon Yazi").trigger(synchro_processing);
		yazi_synchro.poss(move(yazi, -1, 2));
		Action yazi_mats = action("Check for Yazi mats").open().hopt().trigger(yazi_synchro).poss(7, move("non tuner", 2, 6), move("tuner", 2, 6));
		yazi_mats.poss(7, move(token, 2, -1), move("non tuner", 2, 6));
		yazi_mats.poss(move("tuner", 2, 6), move(2, dontoken, 2, -1));
		yazi_mats.poss(7, move(token, 2, -1), move(dontoken, 2, -1));
		Action yazi_eff = action("Yazi eff summon from deck").hopt().poss(move("swordsoul monster", 0, 2));
		Action chixiao_synchro = action("Synchro Summon Chi Xiao").trigger(synchro_processing);
		chixiao_synchro.poss(move(chixiao, -1, 2));
		Action chixiao_mats = action("Check for Chi Xiao mats").open().hopt().trigger(chixiao_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
		chixiao_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
		Action chixiao_eff = action("Chi Xiao Summon Eff").hopt();
		chixiao_eff.poss(move("swordsoul", 0, 1));
//		chixiao_summon.poss(move("swordsoul", 0, 5));
		MT.add(chixiao, -1, 2, chixiao_eff);
//		Action chengying_synchro = action("Synchro Summon Cheng Ying").trigger(synchro_processing);
//		chengying_synchro.poss(move(chengying, -1, 2));
//		Action chengying_mats = action("Check for Cheng Ying mats").open().hopt().trigger(chengying_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
//		chengying_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
//		Action berserker_synchro = action("Synchro Summon Draco Berserker").trigger(synchro_processing);
//		berserker_synchro.poss(move(berserker, -1, 2));
//		Action berserker_mats = action("Check for Draco Berserker mats").open().hopt().trigger(berserker_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		berserker_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action dragite_synchro = action("Synchro Summon Dragite").trigger(synchro_processing);
//		dragite_synchro.poss(move(dragite, -1, 2));
//		Action dragite_mats = action("Check for Dragite mats").open().hopt().trigger(dragite_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		dragite_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_synchro = action("Synchro Summon Baxia").trigger(synchro_processing);
//		baxia_synchro.poss(move(baxia, -1, 2));
//		Action baxia_mats = action("Check for Baxia mats").open().hopt().trigger(baxia_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		baxia_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_reborn = action("Baxia reborn eff").open().hopt();
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("bad ns", 4, 2));
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("good ns", 4, 2));
//		Action chaofeng_synchro = action("Synchro Summon Chaofeng").trigger(synchro_processing);
//		chaofeng_synchro.poss(move(chaofeng, -1, 2));
//		Action chaofeng_mats = action("Check for Chaofeng mats").open().hopt().trigger(chaofeng_synchro).poss(9, move("yang zing", 2, 6), move("tuner", 2, 6));
		Action baronne_synchro = action("Synchro Summon Baronne de Fleur").trigger(synchro_processing);
		baronne_synchro.poss(move(baronne, -1, 2));
		Action baronne_mats = action("Check for Baronne mats").open().hopt().trigger(baronne_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
		baronne_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
		baronne_mats.poss(move(3, dontoken, 2, -1), move(deskbot, 2, 6));
		baronne_mats.poss(move(token, 2, -1), move(2, dontoken, 2, -1));
		
		Action monk_link = action("Link Summon Monk").open().hopt().poss(move("tenyi", 2, 4).exclude(monk), move(monk, -1, 2));
		Action halq_link = action("Link Summon Halqifibrax").open().hopt().poss(move("tuner", 2, 4), move("card", 2, 4).exclude("token"), move(halq, -1, 2));
		Action auroradon_link = action("Link Summon Auroradon").open().hopt().poss(move(halq, 2, 4), move(deskbot, 2, 4), move(auroradon, -1, 2));
		Action halq_eff = action("Halqifibrax eff summon from deck").hopt().poss(move(deskbot, list(0, 1), 2));
		Action auroradon_eff = action("Auroradon eff summon tokens").hopt().trigger(deskbot_eff).poss(move(3, dontoken, -1, 2)).turnoff(monk_link);
		MT.add(halq, -1, 2, halq_eff);
		MT.add(auroradon, -1, 2, auroradon_eff);
		Action auroradon_pop = action("Auroradon eff pop a card").hopt().open().trigger(yazi_eff).poss(cond(auroradon, 2), move(dontoken, 2, -1), move(yazi, 2, 4));
//		auroradon_pop.poss(move(auroradon, 2, 4), move(yazi, 2, 4));
		
		Action moye_draw = action("Mo Ye GY eff").hopt().draw(1, 1);
		MT.add(moye, 2, 6, moye_draw);
		Action taia_mill = action("Tai A GY eff").hopt();
		taia_mill.poss(move(adhara, 0, 4));
		taia_mill.poss(move(moye, 0, 4));
//		taia_mill.poss(cond(summit, 1), move(moye, 0, 4));
//		taia_mill.poss(zcond(summit, 1), move(adhara, 0, 4));
		MT.add(taia, 2, 6, taia_mill);
//		Action taia_banish = action("Tai A GY eff").hopt().off();
//		taia_banish.poss(move("wyrm", 0, 5).exclude(taia));
//		MT.add(taia, 2, 6, taia_banish);
		
		Action ashuna_summon = action("Ashuna summon from hand").open().hopt().poss(zcond("effect", 2), move(ashuna, 1, 2));
		Action vishudda_summon = action("Vishudda summon from hand").open().hopt().poss(zcond("effect", 2), move(vishudda, 1, 2));
		Action shthana_summon = action("Shthana summon from hand").open().hopt().poss(zcond("effect", 2), move(shthana, 1, 2));
		Action adhara_summon = action("Adhara summon from hand").open().hopt().poss(zcond("effect", 2), move(adhara, 1, 2));
		Action ashuna_gy = action("Ashuna eff in gy").open().hopt();
		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna));
//		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna).exclude(shthana));
//		ashuna_gy.poss(cond("non effect", 2), cond("lvl 7", 2), move(ashuna, 4, 5), move(adhara, 0, 2));
//		ashuna_gy.poss(cond("non effect", 2), cond("lvl 1 tuner", 2), move(ashuna, 4, 5), move(vishudda, 0, 2));
//		ashuna_gy.poss(cond("non effect", 2), cond(token, 2), move(ashuna, 4, 5), move(shthana, 0, 2));
//		ashuna_gy.turnoff(herald_synchro).turnoff(baronne_synchro).turnoff(dragite_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
//		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link);
//		ashuna_gy.turnoff(baronne_synchro);
		Action adhara_gy = action("Adhara eff in gy").open().hopt().poss(move(adhara, 4, 5), move("wyrm", 5, 1).exclude(adhara).exclude("link").exclude("synchro"));
		
//		Action herald_floodgate = action("Herald banish on").turnoff(taia_mill).turnon(taia_banish).turnoff(longyuan_eff).turnon(longyuan_eff_banish);
//		MT.add(herald, -1, 2, herald_floodgate);
//		Action herald_unfloodgate = action("Herald banish off").turnoff(taia_banish).turnon(taia_mill).turnon(longyuan_eff).turnoff(longyuan_eff_banish);
//		MT.add(herald, 2, 4, herald_unfloodgate);
		
//		Action token_lock = action("Token Lock").turnoff(monk_link).turnoff(halq_link).turnoff(auroradon_link);
		Action token_lock = action("Token Lock").turnoff(monk_link);
		MT.add(token, -1, 2, token_lock);
		
//		Action token_unlock = action("Token Unlock").turnon(monk_link).turnon(halq_link).turnon(auroradon_link);
		Action token_unlock = action("Token Unlock").turnon(monk_link);
		MT.add(token,  2, -1, token_unlock);
		
//		Gov.poss(cond(chixiao, 2), cond(blackout, 3));
//		Gov.poss(cond("handtrap", 1), cond(chixiao, 2), cond(baronne, 2));
//		Gov.terminate(cond(1, '-', "handtrap", 1));
//		Gov.terminate(zcond("handtrap", 1));
//		Gov.poss(cond(chaofeng, 2), cond(chixiao, 2), cond(blackout, 3));
		Gov.poss(cond(baronne, 2), cond(chixiao, 2), cond(blackout, 3));
		
		go("Chi Xiao + Baronne + Blackout Halq 2 Vessel 41 Cards", 1000);
//		go("Chi Xiao + Chaofeng + Blackout Circle", 1000);
//		go("Chi Xiao + Blackout", 1000);
	}
	public static void bmain(String args[])
	{
		Card moye = card("Mo Ye", 3, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card taia  = card("Tai A", 2, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card longyuan = card("Long Yuan", 3, 6, "wyrm", "swordsoul", "swordsoul monster", "effect", "non tuner");
		Card dragonsword = card("Dragonsword", 3, "swordsoul");
		Card ashuna = card("Ashuna", 3, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card vishudda = card("Vishudda", 2, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card adhara  = card("Adhara", 3, 1, "wyrm", "tenyi", "bad ns", "tuner", "effect");
		Card vessel = card("Vessel", 3);
		Card blackout = card("Blackout", 1, "swordsoul");
//		Card summit = card("Summit", 1, "swordsoul");
//		Card crossout = card("Crossout", 3);
		Card cbtg = card("CBTG", 1, "handtrap");
		Card desires = card("Pot of Desires", 1);
		Card ash = card("Ash", 3, 3, "bad ns", "tuner", "effect", "handtrap");
		Card ogre = card("Ogre", 3, 3, "bad ns", "tuner", "psychic", "effect", "handtrap");
		Card belle = card("Belle", 2, 3, "bad ns", "tuner", "effect", "handtrap");
		Card veiler = card("Veiler", 2, 1, "bad ns", "tuner", "effect", "handtrap");
		Card imperm = card("Imperm", 2, "handtrap");
//		Card cosmic = card("Cosmic Cyclone", 2, "handtrap");
		Card nibiru = card("Nibiru", 3, 11, "handtrap", "effect");
		Card shthana = card("Shthana", 1, "bad ns", "wyrm", "tenyi", "lvl 4", "effect");
		Card circle = card("Circle", 3);
		Card deskbot = card("Deskbot 001", 1, 1, "bad ns", "tuner", "effect");
		Card ecclesia = card("Incredible Ecclesia", 3, 4, "good ns", "tuner", "effect");
		Card etele = card("Emergency Teleport", 0);
//		Card gamma = card("Psy-Framegear Gamma", 3, 2, "handtrap", "tuner", "effect", "psychic");
//		Card driver = card("Psy-Frame Driver", 1, 6, "psychic");
		
		Card monk = card("Monk", 0, "wyrm", "tenyi", "link", "non effect");
		Card halq = card("Halqifibrax", 0, "link", "effect");
		Card auroradon = card("Auroradon", 0, "link", "effect");
		Card chixiao = card("Chi Xiao", 0, 8, "wyrm", "swordsoul", "synchro", "effect");
		Card baronne = card("Baronne", 0, 10, "synchro", "effect");
//		Card chengying = card("Cheng Ying", 0, 10, "wyrm", "swordsoul", "synchro", "effect");
//		Card baxia = card("Baxia", 0, 8, "wyrm", "yang zing", "synchro", "effect");
//		Card berserker = card("Draco Berserker", 0, 8, "wyrm", "tenyi", "synchro", "effect");
//		Card chaofeng = card("Chaofeng", 0, 9, "wyrm", "yang zing", "synchro", "effect");
//		Card dragite = card("Dragite", 0, 8, "synchro", "effect");
		Card yazi = card("Yazi", 0, 7, "wyrm", "yang zing", "synchro", "effect");
//		Card herald = card("Herald of the Arc Light", 0, 4, "synchro", "effect");
		
		Card token = card("Swordsoul Token", 0, 4, "token", "swordsoul", "non effect", "lvl 4 tuner");
		Card dontoken = card("Auroradon Token", 0, 3, "token", "non effect", "lvl 3");
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Synchro processing", "Desires zone");
		hand(5);
		
		Action draw_second = action("Draw for turn").open().hopt().poss(zcond("card", 2)).draw(1, 1);
		
		Action dragonsword_eff = action("Dragonsword eff").open().hopt().poss(move(dragonsword, 1, 4), move("swordsoul monster", 0, 1));
//		Action summit_eff = action("Summit eff").open().hopt().poss(move(summit, 1, 4), move("swordsoul monster", 4, 2));
		
		Action vessel_eff = action("Vessel eff").open().hopt();
		vessel_eff.poss(move(vessel, 1, 4), cond("non effect", 2), move("tenyi", 0, 4).exclude(vishudda), move("tenyi", 0, 1));
		
		
		Action circle_eff = action("Circle eff").open().hopt();
//		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2));
//		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1));
		circle_eff.poss(cond("non effect", 2), move(circle, 4, 5), move("tenyi", 0, 1));
		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2).exclude(shthana));
		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1).exclude(shthana));
		
		
		Action blackout_eff = action("Blackout summon token").hopt().poss(move(token, -1, 2));
		MT.add(blackout, list(0, 1, 3, 4), 5, blackout_eff);
		Action blackout_set = action("Set Blackout").open().hopt().poss(move(blackout, 1, 3));
		Action desires_eff = action("Activate Desires").open().hopt().poss(move(desires, 1, 4)).draw(7, 10).draw(1,2);
		Action etele_eff = action("Activate E Tele").open().poss(cond("card", 2), move(etele, 1, 4), move(ogre, list(0, 1), 2));
		action("Normal Summon").open().hopt().poss(move("good ns", 1, 2)).poss(zcond("good ns", 1), move("bad ns", 1, 2));
		
		
		Action moye_summon = action("Mo Ye summon token").hopt();
		moye_summon.poss(cond("tenyi", 1), move(token, -1, 2));
		moye_summon.poss(zcond("tenyi", 1), cond("swordsoul", 1), move(token, -1, 2));
		MT.add(moye, list(0,1,4), 2, moye_summon);
		Action taia_summon = action("Tai A summon token").open().hopt();
		taia_summon.poss(cond(taia, 2), move("tenyi", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), zcond("tenyi", 4), move("swordsoul", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), move("yang zing", 4, 5), move(token, -1, 2));
		Action longyuan_eff = action("Long Yuan eff in hand").open().hopt();
		longyuan_eff.poss(zcond("tenyi", 1), move("swordsoul", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff.poss(move("tenyi", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		Action longyuan_eff_banish = action("Long Yuan eff in hand").open().hopt().off();
		longyuan_eff_banish.poss(zcond("tenyi", 1), move("swordsoul", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff_banish.poss(move("tenyi", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		Action ecclesia_eff = action("Ecclesia summon from deck").open().hopt().poss(move(ecclesia, 2, 4), move("swordsoul monster", 0, 2).exclude(longyuan));
		Action deskbot_eff = action("Deskbot 001 reborn self").hopt().poss(move(deskbot, 4, 2));
		
		Action synchro_processing = action("Synchro processing").move_all(6, 4);
//		Action herald_synchro = action("Synchro Summon Herald of the Arc Light").trigger(synchro_processing);
//		herald_synchro.poss(move(herald, -1, 2));
//		Action herald_mats = action("Check for Herald mats").open().hopt().trigger(herald_synchro);
//		herald_mats.poss(4, move("tuner", 2, 4), move(dontoken, 2, -1));
		Action yazi_synchro = action("Synchro Summon Yazi").trigger(synchro_processing);
		yazi_synchro.poss(move(yazi, -1, 2));
		Action yazi_mats = action("Check for Yazi mats").open().hopt().trigger(yazi_synchro).poss(7, move("non tuner", 2, 6), move("tuner", 2, 6));
		yazi_mats.poss(7, move(token, 2, -1), move("non tuner", 2, 6));
		yazi_mats.poss(move("tuner", 2, 6), move(2, dontoken, 2, -1));
		yazi_mats.poss(7, move(token, 2, -1), move(dontoken, 2, -1));
		Action yazi_eff = action("Yazi eff summon from deck").hopt().poss(move("swordsoul monster", 0, 2));
		Action chixiao_synchro = action("Synchro Summon Chi Xiao").trigger(synchro_processing);
		chixiao_synchro.poss(move(chixiao, -1, 2));
		Action chixiao_mats = action("Check for Chi Xiao mats").open().hopt().trigger(chixiao_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
		chixiao_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
		Action chixiao_eff = action("Chi Xiao Summon Eff").hopt();
		chixiao_eff.poss(move("swordsoul", 0, 1));
//		chixiao_summon.poss(move("swordsoul", 0, 5));
		MT.add(chixiao, -1, 2, chixiao_eff);
//		Action chengying_synchro = action("Synchro Summon Cheng Ying").trigger(synchro_processing);
//		chengying_synchro.poss(move(chengying, -1, 2));
//		Action chengying_mats = action("Check for Cheng Ying mats").open().hopt().trigger(chengying_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
//		chengying_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
//		Action berserker_synchro = action("Synchro Summon Draco Berserker").trigger(synchro_processing);
//		berserker_synchro.poss(move(berserker, -1, 2));
//		Action berserker_mats = action("Check for Draco Berserker mats").open().hopt().trigger(berserker_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		berserker_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action dragite_synchro = action("Synchro Summon Dragite").trigger(synchro_processing);
//		dragite_synchro.poss(move(dragite, -1, 2));
//		Action dragite_mats = action("Check for Dragite mats").open().hopt().trigger(dragite_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		dragite_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_synchro = action("Synchro Summon Baxia").trigger(synchro_processing);
//		baxia_synchro.poss(move(baxia, -1, 2));
//		Action baxia_mats = action("Check for Baxia mats").open().hopt().trigger(baxia_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		baxia_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_reborn = action("Baxia reborn eff").open().hopt();
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("bad ns", 4, 2));
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("good ns", 4, 2));
//		Action chaofeng_synchro = action("Synchro Summon Chaofeng").trigger(synchro_processing);
//		chaofeng_synchro.poss(move(chaofeng, -1, 2));
//		Action chaofeng_mats = action("Check for Chaofeng mats").open().hopt().trigger(chaofeng_synchro).poss(9, move("yang zing", 2, 6), move("tuner", 2, 6));
		Action baronne_synchro = action("Synchro Summon Baronne de Fleur").trigger(synchro_processing);
		baronne_synchro.poss(move(baronne, -1, 2));
		Action baronne_mats = action("Check for Baronne mats").open().hopt().trigger(baronne_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
		baronne_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
		baronne_mats.poss(move(3, dontoken, 2, -1), move(deskbot, 2, 6));
		baronne_mats.poss(move(token, 2, -1), move(2, dontoken, 2, -1));
		
		Action monk_link = action("Link Summon Monk").open().hopt().poss(move("tenyi", 2, 4).exclude(monk), move(monk, -1, 2));
		Action halq_link = action("Link Summon Halqifibrax").open().hopt().poss(move("tuner", 2, 4), move("card", 2, 4).exclude("token"), move(halq, -1, 2));
		Action auroradon_link = action("Link Summon Auroradon").open().hopt().poss(move(halq, 2, 4), move(deskbot, 2, 4), move(auroradon, -1, 2));
		Action halq_eff = action("Halqifibrax eff summon from deck").hopt().poss(move(deskbot, list(0, 1), 2));
		Action auroradon_eff = action("Auroradon eff summon tokens").hopt().trigger(deskbot_eff).poss(move(3, dontoken, -1, 2)).turnoff(monk_link);
		MT.add(halq, -1, 2, halq_eff);
		MT.add(auroradon, -1, 2, auroradon_eff);
		Action auroradon_pop = action("Auroradon eff pop a card").hopt().open().trigger(yazi_eff).poss(cond(auroradon, 2), move(dontoken, 2, -1), move(yazi, 2, 4));
//		auroradon_pop.poss(move(auroradon, 2, 4), move(yazi, 2, 4));
		
		Action moye_draw = action("Mo Ye GY eff").hopt().draw(1, 1);
		MT.add(moye, 2, 6, moye_draw);
		Action taia_mill = action("Tai A GY eff").hopt();
		taia_mill.poss(move(adhara, 0, 4));
		taia_mill.poss(move(moye, 0, 4));
//		taia_mill.poss(cond(summit, 1), move(moye, 0, 4));
//		taia_mill.poss(zcond(summit, 1), move(adhara, 0, 4));
		MT.add(taia, 2, 6, taia_mill);
//		Action taia_banish = action("Tai A GY eff").hopt().off();
//		taia_banish.poss(move("wyrm", 0, 5).exclude(taia));
//		MT.add(taia, 2, 6, taia_banish);
		
		Action ashuna_summon = action("Ashuna summon from hand").open().hopt().poss(zcond("effect", 2), move(ashuna, 1, 2));
		Action vishudda_summon = action("Vishudda summon from hand").open().hopt().poss(zcond("effect", 2), move(vishudda, 1, 2));
		Action shthana_summon = action("Shthana summon from hand").open().hopt().poss(zcond("effect", 2), move(shthana, 1, 2));
		Action adhara_summon = action("Adhara summon from hand").open().hopt().poss(zcond("effect", 2), move(adhara, 1, 2));
		Action ashuna_gy = action("Ashuna eff in gy").open().hopt();
//		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna));
		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna).exclude(shthana));
//		ashuna_gy.turnoff(herald_synchro).turnoff(baronne_synchro).turnoff(dragite_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
//		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link);
//		ashuna_gy.turnoff(baronne_synchro);
		Action adhara_gy = action("Adhara eff in gy").open().hopt().poss(move(adhara, 4, 5), move("wyrm", 5, 1).exclude(adhara).exclude("link").exclude("synchro"));
		
//		Action herald_floodgate = action("Herald banish on").turnoff(taia_mill).turnon(taia_banish).turnoff(longyuan_eff).turnon(longyuan_eff_banish);
//		MT.add(herald, -1, 2, herald_floodgate);
//		Action herald_unfloodgate = action("Herald banish off").turnoff(taia_banish).turnon(taia_mill).turnon(longyuan_eff).turnoff(longyuan_eff_banish);
//		MT.add(herald, 2, 4, herald_unfloodgate);
		
		Action token_lock = action("Token Lock").turnoff(monk_link).turnoff(halq_link).turnoff(auroradon_link);
//		Action token_lock = action("Token Lock").turnoff(monk_link);
		MT.add(token, -1, 2, token_lock);
		
		Action token_unlock = action("Token Unlock").turnon(monk_link).turnon(halq_link).turnon(auroradon_link);
//		Action token_unlock = action("Token Unlock").turnon(monk_link);
		MT.add(token,  2, -1, token_unlock);
		
//		Gov.poss(cond(chixiao, 2), cond(blackout, 3));
		Gov.poss(cond("handtrap", 1), cond(chixiao, 2), cond(baronne, 2));
		Gov.terminate(cond(1, '-', "handtrap", 1));
//		Gov.terminate(zcond("handtrap", 1));
//		Gov.poss(cond(chaofeng, 2), cond(chixiao, 2), cond(blackout, 3));
//		Gov.poss(cond(baronne, 2), cond(chixiao, 2), cond(blackout, 3));
		
		DeckEdit[] ch = {new DeckEdit(new Card[] {etele, ecclesia}, new int[] {3, 0}), 
		new DeckEdit(new Card[] {vessel}, new int[] {0})		
		};
		go("Chi Xiao + Baronne + 2 Handtraps 48 Cards", 100, ch);
//		go("Chi Xiao + Chaofeng + Blackout Circle", 1000);
//		go("Chi Xiao + Blackout", 1000);
	}
	
	public static void burritomain(String args[])
	{
		Card moye = card("Mo Ye", 3, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card taia  = card("Tai A", 2, 4, "wyrm", "swordsoul", "swordsoul monster", "good ns", "effect", "non tuner");
		Card longyuan = card("Long Yuan", 3, 6, "wyrm", "swordsoul", "swordsoul monster", "effect", "non tuner");
		Card dragonsword = card("Dragonsword", 3, "swordsoul");
		Card ashuna = card("Ashuna", 3, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card vishudda = card("Vishudda", 2, 7, "wyrm", "tenyi", "effect", "non tuner");
		Card adhara  = card("Adhara", 3, 1, "wyrm", "tenyi", "bad ns", "tuner", "effect");
		Card vessel = card("Vessel", 3);
		Card blackout = card("Blackout", 1, "swordsoul");
//		Card summit = card("Summit", 1, "swordsoul");
//		Card crossout = card("Crossout", 3);
//		Card cbtg = card("CBTG", 1, "handtrap");
		Card desires = card("Pot of Desires", 1);
		Card ash = card("Ash", 3, 3, "bad ns", "tuner", "effect", "handtrap");
		Card ogre = card("Ogre", 2, 3, "bad ns", "tuner", "psychic", "effect", "handtrap");
//		Card veiler = card("Veiler", 3, 1, "bad ns", "tuner", "effect", "handtrap");
//		Card imperm = card("Imperm", 3, "handtrap");
		Card shthana = card("Shthana", 1, "bad ns", "wyrm", "tenyi", "lvl 4", "effect");
		Card circle = card("Circle", 2);
		Card deskbot = card("Deskbot 001", 1, 1, "bad ns", "tuner", "effect");
		Card ecclesia = card("Incredible Ecclesia", 3, 4, "good ns", "tuner", "effect");
		Card etele = card("Emergency Teleport", 3);
		Card cosmic = card("Cosmic Cyclone", 3, "handtrap");
		
		Card monk = card("Monk", 0, "wyrm", "tenyi", "link", "non effect");
		Card halq = card("Halqifibrax", 0, "link", "effect");
		Card auroradon = card("Auroradon", 0, "link", "effect");
		Card chixiao = card("Chi Xiao", 0, 8, "wyrm", "swordsoul", "synchro", "effect");
		Card baronne = card("Baronne", 0, 10, "synchro", "effect");
//		Card chengying = card("Cheng Ying", 0, 10, "wyrm", "swordsoul", "synchro", "effect");
//		Card baxia = card("Baxia", 0, 8, "wyrm", "yang zing", "synchro", "effect");
//		Card berserker = card("Draco Berserker", 0, 8, "wyrm", "tenyi", "synchro", "effect");
//		Card chaofeng = card("Chaofeng", 0, 9, "wyrm", "yang zing", "synchro", "effect");
//		Card dragite = card("Dragite", 0, 8, "synchro", "effect");
		Card yazi = card("Yazi", 0, 7, "wyrm", "yang zing", "synchro", "effect");
//		Card herald = card("Herald of the Arc Light", 0, 4, "synchro", "effect");
		
		Card token = card("Swordsoul Token", 0, 4, "token", "swordsoul", "non effect", "lvl 4 tuner");
		Card dontoken = card("Auroradon Token", 0, 3, "token", "non effect", "lvl 3");
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Synchro processing", "Desires zone");
		hand(5);
		
		Action draw_second = action("Draw for turn").open().hopt().poss(zcond("card", 2)).draw(1, 1);
		
		Action dragonsword_eff = action("Dragonsword eff").open().hopt().poss(move(dragonsword, 1, 4), move("swordsoul monster", 0, 1));
//		Action summit_eff = action("Summit eff").open().hopt().poss(move(summit, 1, 4), move("swordsoul monster", 4, 2));
		
		Action vessel_eff = action("Vessel eff").open().hopt();
		vessel_eff.poss(move(vessel, 1, 4), cond("non effect", 2), move("tenyi", 0, 4).exclude(vishudda), move("tenyi", 0, 1));
		
		vessel_eff.poss(zcond(ashuna, 4), cond("non effect", 2), move(vessel, 1, 4), move(ashuna, 0, 4), move("tenyi", 0, 1));
		vessel_eff.poss(cond(ashuna, 4), cond("non effect", 2), move(vessel, 1, 4), move("tenyi", 0, 4).exclude(ashuna), move("tenyi", 0, 1).exclude(ashuna));
		vessel_eff.poss(cond(taia, 1), move(vessel, 1, 4), move("wyrm", 0, 4));
		
		Action circle_eff = action("Circle eff").open().hopt();
//		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2));
//		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1));
		circle_eff.poss(cond("non effect", 2), move(circle, 4, 5), move("tenyi", 0, 1));
		circle_eff.poss(move(circle, 1, 4), move("non effect", 2, -1), move("tenyi", 0, 2).exclude(shthana));
		circle_eff.poss(move(circle, 1, 4), move("wyrm", 2, 4), move("wyrm", 0, 1).exclude(shthana));
		
		
		Action blackout_eff = action("Blackout summon token").hopt().poss(move(token, -1, 2));
		MT.add(blackout, list(0, 1, 3, 4), 5, blackout_eff);
		Action blackout_set = action("Set Blackout").open().hopt().poss(move(blackout, 1, 3));
		Action desires_eff = action("Activate Desires").open().hopt().poss(move(desires, 1, 4)).draw(7, 10).draw(1,2);
		Action etele_eff = action("Activate E Tele").open().poss(cond("card", 2), move(etele, 1, 4), move(ogre, list(0, 1), 2));
		action("Normal Summon").open().hopt().poss(move("good ns", 1, 2)).poss(zcond("good ns", 1), move("bad ns", 1, 2));
		
		
		Action moye_summon = action("Mo Ye summon token").hopt();
		moye_summon.poss(cond("tenyi", 1), move(token, -1, 2));
		moye_summon.poss(zcond("tenyi", 1), cond("swordsoul", 1), move(token, -1, 2));
		MT.add(moye, list(0,1,4), 2, moye_summon);
		Action taia_summon = action("Tai A summon token").open().hopt();
		taia_summon.poss(cond(taia, 2), move("tenyi", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), zcond("tenyi", 4), move("swordsoul", 4, 5), move(token, -1, 2));
		taia_summon.poss(cond(taia, 2), move("yang zing", 4, 5), move(token, -1, 2));
		Action longyuan_eff = action("Long Yuan eff in hand").open().hopt();
		longyuan_eff.poss(zcond("tenyi", 1), move("swordsoul", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff.poss(move("tenyi", 1, 4), move(longyuan, 1, 2), move(token, -1, 2));
		Action longyuan_eff_banish = action("Long Yuan eff in hand").open().hopt().off();
		longyuan_eff_banish.poss(zcond("tenyi", 1), move("swordsoul", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		longyuan_eff_banish.poss(move("tenyi", 1, 5), move(longyuan, 1, 2), move(token, -1, 2));
		Action ecclesia_eff = action("Ecclesia summon from deck").open().hopt().poss(move(ecclesia, 2, 4), move("swordsoul monster", 0, 2).exclude(longyuan));
		Action deskbot_eff = action("Deskbot 001 reborn self").hopt().poss(move(deskbot, 4, 2));
		
		Action synchro_processing = action("Synchro processing").move_all(6, 4);
//		Action herald_synchro = action("Synchro Summon Herald of the Arc Light").trigger(synchro_processing);
//		herald_synchro.poss(move(herald, -1, 2));
//		Action herald_mats = action("Check for Herald mats").open().hopt().trigger(herald_synchro);
//		herald_mats.poss(4, move("tuner", 2, 4), move(dontoken, 2, -1));
		Action yazi_synchro = action("Synchro Summon Yazi").trigger(synchro_processing);
		yazi_synchro.poss(move(yazi, -1, 2));
		Action yazi_mats = action("Check for Yazi mats").open().hopt().trigger(yazi_synchro).poss(7, move("non tuner", 2, 6), move("tuner", 2, 6));
		yazi_mats.poss(7, move(token, 2, -1), move("non tuner", 2, 6));
		yazi_mats.poss(move("tuner", 2, 6), move(2, dontoken, 2, -1));
		yazi_mats.poss(7, move(token, 2, -1), move(dontoken, 2, -1));
		Action yazi_eff = action("Yazi eff summon from deck").hopt().poss(move("swordsoul monster", 0, 2));
		Action chixiao_synchro = action("Synchro Summon Chi Xiao").trigger(synchro_processing);
		chixiao_synchro.poss(move(chixiao, -1, 2));
		Action chixiao_mats = action("Check for Chi Xiao mats").open().hopt().trigger(chixiao_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
		chixiao_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
		Action chixiao_eff = action("Chi Xiao Summon Eff").hopt();
		chixiao_eff.poss(move("swordsoul", 0, 1));
//		chixiao_summon.poss(move("swordsoul", 0, 5));
		MT.add(chixiao, -1, 2, chixiao_eff);
//		Action chengying_synchro = action("Synchro Summon Cheng Ying").trigger(synchro_processing);
//		chengying_synchro.poss(move(chengying, -1, 2));
//		Action chengying_mats = action("Check for Cheng Ying mats").open().hopt().trigger(chengying_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
//		chengying_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
//		Action berserker_synchro = action("Synchro Summon Draco Berserker").trigger(synchro_processing);
//		berserker_synchro.poss(move(berserker, -1, 2));
//		Action berserker_mats = action("Check for Draco Berserker mats").open().hopt().trigger(berserker_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		berserker_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action dragite_synchro = action("Synchro Summon Dragite").trigger(synchro_processing);
//		dragite_synchro.poss(move(dragite, -1, 2));
//		Action dragite_mats = action("Check for Dragite mats").open().hopt().trigger(dragite_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		dragite_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_synchro = action("Synchro Summon Baxia").trigger(synchro_processing);
//		baxia_synchro.poss(move(baxia, -1, 2));
//		Action baxia_mats = action("Check for Baxia mats").open().hopt().trigger(baxia_synchro).poss(8, move("non tuner", 2, 6), move("tuner", 2, 6));
//		baxia_mats.poss(8, move(token, 2, -1), move("non tuner", 2, 6));
//		Action baxia_reborn = action("Baxia reborn eff").open().hopt();
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("bad ns", 4, 2));
//		baxia_reborn.poss(cond(baxia, 2), move("card", 2, 4), move("good ns", 4, 2));
//		Action chaofeng_synchro = action("Synchro Summon Chaofeng").trigger(synchro_processing);
//		chaofeng_synchro.poss(move(chaofeng, -1, 2));
//		Action chaofeng_mats = action("Check for Chaofeng mats").open().hopt().trigger(chaofeng_synchro).poss(9, move("yang zing", 2, 6), move("tuner", 2, 6));
		Action baronne_synchro = action("Synchro Summon Baronne de Fleur").trigger(synchro_processing);
		baronne_synchro.poss(move(baronne, -1, 2));
		Action baronne_mats = action("Check for Baronne mats").open().hopt().trigger(baronne_synchro).poss(10, move("non tuner", 2, 6), move("tuner", 2, 6));
		baronne_mats.poss(10, move(token, 2, -1), move("non tuner", 2, 6));
		baronne_mats.poss(move(3, dontoken, 2, -1), move(deskbot, 2, 6));
		baronne_mats.poss(move(token, 2, -1), move(2, dontoken, 2, -1));
		
		Action monk_link = action("Link Summon Monk").open().hopt().poss(move("tenyi", 2, 4).exclude(monk), move(monk, -1, 2));
		Action halq_link = action("Link Summon Halqifibrax").open().hopt().poss(move("tuner", 2, 4), move("card", 2, 4).exclude("token"), move(halq, -1, 2));
		Action auroradon_link = action("Link Summon Auroradon").open().hopt().poss(move(halq, 2, 4), move(deskbot, 2, 4), move(auroradon, -1, 2));
		Action halq_eff = action("Halqifibrax eff summon from deck").hopt().poss(move(deskbot, list(0, 1), 2));
		Action auroradon_eff = action("Auroradon eff summon tokens").hopt().trigger(deskbot_eff).poss(move(3, dontoken, -1, 2)).turnoff(monk_link);
		MT.add(halq, -1, 2, halq_eff);
		MT.add(auroradon, -1, 2, auroradon_eff);
		Action auroradon_pop = action("Auroradon eff pop a card").hopt().open().trigger(yazi_eff).poss(cond(auroradon, 2), move(dontoken, 2, -1), move(yazi, 2, 4));
//		auroradon_pop.poss(move(auroradon, 2, 4), move(yazi, 2, 4));
		
		Action moye_draw = action("Mo Ye GY eff").hopt().draw(1, 1);
		MT.add(moye, 2, 6, moye_draw);
		Action taia_mill = action("Tai A GY eff").hopt();
		taia_mill.poss(move(ashuna, 0, 4).exclude(taia));
//		taia_mill.poss(cond(summit, 1), move(moye, 0, 4));
//		taia_mill.poss(zcond(summit, 1), move(adhara, 0, 4));
		MT.add(taia, 2, 6, taia_mill);
		Action taia_banish = action("Tai A GY eff").hopt().off();
		taia_banish.poss(move("wyrm", 0, 5).exclude(taia));
		MT.add(taia, 2, 6, taia_banish);
		
		Action ashuna_summon = action("Ashuna summon from hand").open().hopt().poss(zcond("effect", 2), move(ashuna, 1, 2));
		Action vishudda_summon = action("Vishudda summon from hand").open().hopt().poss(zcond("effect", 2), move(vishudda, 1, 2));
		Action shthana_summon = action("Shthana summon from hand").open().hopt().poss(zcond("effect", 2), move(shthana, 1, 2));
		Action adhara_summon = action("Adhara summon from hand").open().hopt().poss(zcond("effect", 2), move(adhara, 1, 2));
		Action ashuna_gy = action("Ashuna eff in gy").open().hopt();
//		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna));
		ashuna_gy.poss(cond("non effect", 2), move(ashuna, 4, 5), move("tenyi", 0, 2).exclude(ashuna).exclude(shthana));
		
//		ashuna_gy.turnoff(herald_synchro).turnoff(baronne_synchro).turnoff(dragite_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link).turnoff(etele_eff);
//		ashuna_gy.turnoff(baronne_synchro).turnoff(halq_link).turnoff(auroradon_link);
		Action adhara_gy = action("Adhara eff in gy").open().hopt().poss(move(adhara, 4, 5), move("wyrm", 5, 1).exclude(adhara).exclude("link").exclude("synchro"));
		
//		Action herald_floodgate = action("Herald banish on").turnoff(taia_mill).turnon(taia_banish).turnoff(longyuan_eff).turnon(longyuan_eff_banish);
//		MT.add(herald, -1, 2, herald_floodgate);
//		Action herald_unfloodgate = action("Herald banish off").turnoff(taia_banish).turnon(taia_mill).turnon(longyuan_eff).turnoff(longyuan_eff_banish);
//		MT.add(herald, 2, 4, herald_unfloodgate);
		
		Action token_lock = action("Token Lock").turnoff(monk_link).turnoff(halq_link).turnoff(auroradon_link);
		MT.add(token, -1, 2, token_lock);
		
		Action token_unlock = action("Token Unlock").turnon(monk_link).turnon(halq_link).turnon(auroradon_link);
		MT.add(token,  2, -1, token_unlock);
		
//		Gov.poss(cond(chixiao, 2), cond(blackout, 3));
		Gov.poss(cond("handtrap", 1), cond(chixiao, 2), cond(baronne, 2));
		Gov.terminate(cond(1, '-', "handtrap", 1));
//		Gov.terminate(zcond("handtrap", 1));
//		Gov.poss(cond(chaofeng, 2), cond(chixiao, 2), cond(blackout, 3));
//		Gov.poss(cond(baronne, 2), cond(chixiao, 2), cond(blackout, 3));
		
		go("Chi Xiao + Baronne + 2 Handtraps Halq Burritoman93 Build", 5000);
//		go("Chi Xiao + Chaofeng + Blackout Circle", 1000);
//		go("Chi Xiao + Blackout", 1000);
	}
	public static void mainT(String args[])
	{
		Card rescue = card("Rescue Cat", 3, "tri-type", "monster");
		Card frak = card("TB Fraktall", 3, "tb", "tri-type", "monster", "main tb");
		Card kitt = card("TB Kitt", 3, "tb", "tri-type", "monster", "cat_summon", "good_gy", "main tb");
		Card nerv = card("TB Nervall", 3, "tb", "tri-type", "monster", "good_gy", "main tb");
		Card keras = card("TB Keras", 2, "tb", "tri-type", "monster", "cat_summon", "main tb");
		Card ash_veiler = card("ash/veiler",5, "monster", "first_trap", "second_trap", "altnormal");
		Card gamma = card("Gamma", 3, "monster", "second_trap");
		Card driver = card("Driver",1);
		Card imperm = card("Impermanence", 2, "first_trap", "second_trap");
		Card strike = card("Solemn Strike", 2, "first_trap");
		Card revolt = card("TB Revolt", 2);
		Card imporder = card("Imperial Order", 1);
		Card dheroes = card("DFusion brick", 2);
		Card Dfusion = card("Fusion Destiny", 3);
		Card tenki = card("Tenki", 1);
		Card cbtg = card("cbtg",1);
		Card prosperity = card("Pot of Prosperity",3);
		//Card desires = card("Desires", 0);
		
		Card almiraj = card("Almiraj", 0);
		Card ferrijit = card("Ferrijit", 0, "tb", "tri-type", "tb link-2", "ferrijit");
		Card brumm = card("Bearbrumm", 0, "tb", "tri-type", "tb link-2", "brumm");
		Card rugal = card("Rugal", 0, "tb", "tri-type", "tb link-3", "rugal", "good_with_rev");
		Card omen =  card("Shuraig", 0, "tb", "tri-type", "tb link-4", "omen");
		Card fake_ferrijit = card("Imp Ferrijit", 0, "fake tb", "tri-type", "tb link-2", "ferrijit");
		Card fake_brumm = card("Imp Bearbrumm", 0, "fake tb", "tri-type", "tb link-2", "brumm");
		Card fake_rugal = card("Imp Rugal", 0, "fake tb", "tri-type", "tb link-3", "rugal", "good_with_rev");
		Card fake_omen =  card("Imp Shuraig", 0, "fake tb", "tri-type", "tb link-4", "omen");
		Card verte = card("Verte", 0);
		Card enforcer = card("Enforcer",0);
		/*Card apo2 = card("Apollousa-2", 0 , "apo", "good_with_rev");
		Card apo3 = card("Apollousa-3", 0 , "apo", "good_with_rev");
		Card apo4 = card("Apollousa-4",0, "apo", "good_with_rev");*/
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Prosp Zone", "Desires zone");
		hand(5);
		
		Action place_revolt = action("Place revolt").open().hopt().poss(move(revolt,1,3));
		
		action("Normal Summon").open().hopt().poss(move("tri-type", 1, 2));
		Action frak_eff = action("Frak eff").open().hopt().poss(move(frak,1,4), move("tb",0,4).exclude(frak));
		Action kitt_eff = action("Kitt send").hopt().poss(move(nerv,0,4));
		Action nerv_eff = action("Nerv add").hopt().poss(move("tb",0,1).exclude(nerv));
		MT.add(kitt, list(0,1,2), 4, kitt_eff);
		MT.add(nerv, list(0,1,2), 4, nerv_eff);
		action("Keras eff").open().hopt().poss(move(keras,1,2), move("tri-type", 1, 4));
		
		Action fraktall_summon = action("Frak summon link").open().hopt();
		fraktall_summon.poss(cond(frak,2), move(2,"tri-type",4,5), move(fake_ferrijit, -1, 2));
		fraktall_summon.poss(cond(frak,2), move(2,"tri-type",4,5), move(fake_brumm, -1, 2));
		fraktall_summon.poss(cond(frak,2), move(3,"tri-type",4,5), move(fake_rugal, -1, 2));
		fraktall_summon.poss(cond(frak,2), move(4,"tri-type",4,5), move(fake_omen, -1, 2));
		Action kitt_summon = action("Kitt summon link").open().hopt();
		kitt_summon.poss(cond(kitt,2), move(2,"tri-type",4,5), move(fake_ferrijit, -1, 2));
		kitt_summon.poss(cond(kitt,2), move(2,"tri-type",4,5), move(fake_brumm, -1, 2));
		kitt_summon.poss(cond(kitt,2), move(3,"tri-type",4,5), move(fake_rugal, -1, 2));
		kitt_summon.poss(cond(kitt,2), move(4,"tri-type",4,5), move(fake_omen, -1, 2));
		Action nerv_summon = action("Nerv summon link").open().hopt();
		nerv_summon.poss(cond(nerv,2), move(2,"tri-type",4,5), move(fake_ferrijit, -1, 2));
		nerv_summon.poss(cond(nerv,2), move(2,"tri-type",4,5), move(fake_brumm, -1, 2));
		nerv_summon.poss(cond(nerv,2), move(3,"tri-type",4,5), move(fake_rugal, -1, 2));
		nerv_summon.poss(cond(nerv,2), move(4,"tri-type",4,5), move(fake_omen, -1, 2));
		Action keras_summon = action("Keras summon link").open().hopt();
		keras_summon.poss(cond(keras,2), move(2,"tri-type",4,5), move(fake_ferrijit, -1, 2));
		keras_summon.poss(cond(keras,2), move(2,"tri-type",4,5), move(fake_brumm, -1, 2));
		keras_summon.poss(cond(keras,2), move(3,"tri-type",4,5), move(fake_rugal, -1, 2));
		keras_summon.poss(cond(keras,2), move(4,"tri-type",4,5), move(fake_omen, -1, 2));
		
		action("Cat eff").open().hopt().poss(move(rescue,2,4), move(2,"cat_summon", 0, 2));
		action("Tenki eff").open().hopt().poss(move(tenki,1,3), move(frak,0,1));
		Action prosp_eff = action("Prosp eff").open().hopt().poss(move(prosperity,1,4)).draw(6, 6);
		Action prosp_processing = action("Prosp processing").poss(move("card", 6,1)).move_all(6,0);
		prosp_eff.trigger(prosp_processing);
		
		action("Summon almiraj").open().hopt().poss(move(kitt,2,4), move(almiraj,-1,2)).poss(move(nerv,2,4), move(almiraj,-1,2));
		action("Summon Ferrijit").open().poss(move(2,"tri-type",2,4), move(ferrijit,-1,2));
		Action summon_rugal = action("Summon Rugal").open().poss(move(3,"tri-type",2,4), move(rugal,-1,2));
		summon_rugal.poss(cond(2, "tri-type", 2), move("tb link-2", 2,4), move("tri-type", 2,4));
		Action summon_omen = action("Summon Omen").open().poss(move(4,"tri-type",2,4), move(omen,-1,2));
		summon_omen.poss(cond(3, "tri-type", 2), move("tb link-2", 2,4), move(2, "tri-type", 2,4));
		summon_omen.poss(cond(2, "tri-type", 2), move("tb link-3", 2,4), move("tri-type", 2,4));
		
		/*Action summon_apo = action("Summon apo4").open().hopt().poss(move(4, "tri-type", 2,4).distinct(), move(apo4,-1,2));
		summon_apo.poss(cond(3,"tri-type",2),move(1,"tb link-2", 2,4),move(2, "tri-type", 2,4).distinct(), move(apo3,-1,2));
		summon_apo.poss(move(2,"tb link-2", 2,4).distinct(), move(apo2,-1,2)).poss(cond(2,"tri-type",2),move("tb link-3", 2,4),move( "tri-type", 2,4).distinct(), move(apo2,-1,2));*/

		
		Action ferrijit_eff = action("Ferri eff to spec").open().hopt().poss(cond("ferrijit",2), move("tri-type", 1, 2));
		Action ferrijit_draw = action("Ferri eff to draw").hopt().draw(1,1);
		Action put_back = action("Put back 1").poss(move("card",1,0));
		ferrijit_draw.trigger(put_back);
		ferrijit_draw.turnoff(prosp_eff);
		prosp_eff.turnoff(ferrijit_draw);
		MT.add("ferrijit", 2, 4, ferrijit_draw);
		
		
		
		Action brumm_eff = action("Brumm eff to spec").open().hopt().poss(cond("brumm",2), move(2,"card",1,4), move("main tb",5,2));
		Action brumm_add = action("Brumm eff to add").hopt().poss(move(revolt,0,1)).trigger(put_back);
		MT.add("brumm", 2, 4, brumm_add);

		
		
		Action omen_add = action("Omen eff to add").hopt().poss(cond("card", 5), move(nerv, 0,1)).poss(cond(2,"card", 5), move("cat_summon",0,1));
		MT.add("omen", 2, 4, omen_add);
		
		Action enforcer_eff = action("Enforcer pop").hopt().open().poss(cond(enforcer,2), move("brumm",2,4));
		
		
		Action fusion_destiny = action("Fusion Destiny eff").hopt().open().poss(move(Dfusion,1,4),move(2,dheroes, list(0,1),4), move(enforcer,-1,2));
		
		
		action("Summon Verte").open().hopt().poss(cond(Dfusion,0),cond(2,dheroes, list(0,1)),move(2,"tri-type",2,4), move(verte,-1,2));
		Action verte_eff = action("Eff Verte").open().hopt().poss(move(Dfusion,0,4),move(2,dheroes, list(0,1),4), move(enforcer,-1,2), cond(verte,2));
		verte_eff.turnoff_all().turnon(enforcer_eff).turnon(brumm_add).turnon(kitt_eff).turnon(nerv_eff).turnon(frak_eff);
		brumm_add.turnoff_all().turnon(place_revolt).turnon(put_back);
		fusion_destiny.turnoff_all().turnon(enforcer_eff).turnon(brumm_add).turnon(kitt_eff).turnon(nerv_eff).turnon(frak_eff);
		brumm_add.turnoff_all().turnon(place_revolt).turnon(put_back);
		/*Action desires_eff = action("Activate Desires").open().hopt().poss(move(desires, 1, 4)).draw(7, 10).draw(1,2);
		desires_eff.turnoff(prosp_eff);
		prosp_eff.turnoff(desires_eff);*/
		
		
		Gov.poss(cond(enforcer,2),cond(revolt,3), cond(4, "tri-type", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		Gov.poss(cond(enforcer,2),cond(revolt,3), cond(3, "tri-type", list(4,5)).exclude("fake tb"), cond(1,"tb link-2", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		//Gov.poss(cond(revolt,3), cond(4, "tri-type", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		//Gov.poss(cond(revolt,3), cond(3, "tri-type", list(4,5)).exclude("fake tb"), cond(1,"tb link-2", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		//Gov.poss(cond("good_with_rev",2),cond(revolt,3), cond(4, "tri-type", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		//Gov.poss(cond("good_with_rev",2),cond(revolt,3), cond(3, "tri-type", list(4,5)).exclude("fake tb"), cond(1,"tb link-2", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		//Gov.poss(cond("apo",2),cond(enforcer,2),cond(revolt,3), cond(4, "tri-type", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));
		//Gov.poss(cond("apo",2),cond(enforcer,2),cond(revolt,3), cond(3, "tri-type", list(4,5)).exclude("fake tb"), cond(1,"tb link-2", list(4,5)).exclude("fake tb"), cond(1, "good_gy", list(4,5)));

		
		//Gov.terminate(cond(2, revolt, 7));
		//Gov.terminate(cond(dheroes, 7));
		//Gov.terminate(cond(Dfusion, 7));
		
		go("Enforcer + Revolt", 1000);
		
		
	}
	public static void mainB(String args[])
	{
		Card peg = card("Pegasus", 3, "ns", "lv4", "cb", "windlv4");
		Card frak = card("Fraktall", 6, "ns", "lv4", "cb");
		Card cobalt = card("Cobalt", 1, "ns", "lv4", "cb");
		Card zenith = card("Zenith", 3);
		Card dyna = card("Dynatherium", 1, "ns", "lv4", "windlv4");
		Card shield = card("Shield", 3);
		Card rdd = card("rdd", 1);
		Card other = card("blank", 21);
		Card bagooska = card("Bagooska", 0);
		Card desires = card("Desires",3);
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "FD Banish", "Exc");
		hand(5);


		action("Normal Summon").open().hopt().poss(move("ns", 1, 2));
		action("zenith eff").open().hopt().poss(move(zenith, 3, 5), move("cb", 0, 2), move(rdd, 0, 1));
		action("shield eff").open().hopt().poss(move(shield, 1, 5), move("windlv4", 0, 1));
		Action peg_eff = action("Pegasus eff").poss(move(zenith, list(0,1,4),3));
		MT.add(peg, list(1,4), 2, peg_eff);
		action("Dyna spec").open().hopt().poss(move(dyna, 1, 2));
		action("Summon Bagooska").open().poss(move(2, "lv4", 2, 4), move(bagooska, -1, 2));
		
		Gov.poss(cond(bagooska, 2));
		
		go("bagooska", 10000);
	}
	public static void Fmain(String args[])
	{
		Card dog = card("Fluffal Dog", 3, "fluffal",   "ns", "monster", "lv4");
		Card bear = card("Fluffal Bear", 2, "fluffal", "ns", "monster");
		Card dolphin = card("Fluffal Dolphin", 2, "fluffal",  "lv4water", "ns", "monster", "lv4");
		Card sheep = card("Fluffal Sheep", 1, "fluffal",  "ns", "monster");
		Card wings = card("Fluffal Wings", 1, "fluffal",  "ns", "monster", "good_discard");
		Card penguin = card("Fluffal Penguin", 3, "fluffal",  "lv4water", "ns", "monster", "lv4", "peng");
		Card penguin3 = card("lv3 Fluffal Penguin", 0, "fluffal", "peng", "monster");
		Card octo = card("Fluffal Octo", 1, "fluffal", "ns", "monster");
		Card vendor = card("Toy Vendor", 3, "goods_target", "s/t", "good_discard");
		Card repair = card("Frightfur Repair", 1, "frightfur",  "goods_target", "s/t", "good_discard");
		Card patchwork = card("Frightfur Patchwork", 3, "frightfur", "s/t");
		Card frightfur_fus = card("Frightfur Fusion", 1, "frightfur", "s/t");
		Card poly = card("Polymerization", 3, "s/t");
		Card goods = card("Foolish Burial Goods", 3, "s/t");
		Card chain = card("Edge Imp Chain", 3, "edge", "ns", "monster", "lv4", "good_discard");
		Card edge_scythe = card("Edge Imp Scythe", 3, "edge", "ns", "monster");
		Card sabres = card("Edge Imp Sabres", 1, "edge", "ns", "monster", "good_discard");
		Card gardens = card("Royal Penguin Gardens", 3, "s/t");
		Card driver = card("Psy-Frame Driver", 1, "monster");
		Card gamma = card("Psy-Framegear Gamma", 3, "monster", "dugcross_spec");
		Card called = card("Called by the Grave", 1, "s/t");
		Card arti_scythe = card("Artifact Scythe", 1, "monster");
		Card eldlich = card("Eldlich the Golden Lord", 2, "monster");
		Card tornado = card("Tornado Dragon", 0);
		Card dagda = card("Artifact Dagda", 0, "link 2");
		Card dugares = card("Dugares the Timeless", 0);
		Card whale = card("Frightfur Whale", 0, "fusion");
		Card tiger = card("Frightfur Tiger", 0, "fusion");
		Card shark = card("Bahamut Shark", 0);
		Card toad = card("Toadally Awesome", 0);
		Card cross = card("Cross Sheep", 0, "link 2");
		Card apo3 = card("Apollousa 3", 0, "apo");
		Card apo4 = card("Apollousa 4", 0, "apo");
		//Card desires = card("Desires",3);
		
		locations("Deck", "Hand", "Monster Zone", "S/T Zone", "GY", "Banish", "Vendor Hit", "Fusion Processing", "FD Banish");
		hand(5);

		
		Action normal_summon = action("normal summon").hopt().open().poss(move("ns", 1, 2));
		
		Action dog_search = action("Search off dog").hopt().poss(move("fluffal", 0,1).exclude(dog)).poss(move(sabres, 0,1));
		MT.add(dog, 1, 2, dog_search);
		
		Action vendor_search = action("Search off vendor").poss(move("fluffal", 0,1)).poss(move(sabres, 0,1));
		MT.add(vendor, list(0,1,3,6), 4, vendor_search);
		
		Action bear_eff = action("Bear set vendor").hopt().open().poss(move(bear, 1, 4), move(vendor, 0, 3));
		
		Action penguin_special = action("Special off penguin").hopt().open().off().poss(cond("peng", 2), (move("fluffal", 1, 2)).exclude(penguin).exclude(penguin3));
		
		Action penguin_special_turnon = action("Turn on penguin's special summon").turnon(penguin_special);
		MT.add("peng", list(0,1,4), 2, penguin_special_turnon);
		
		Action penguin_draw = action("Draw 2 for penguin").hopt().draw(1,2);
		MT.add(penguin, 7, 4, penguin_draw);
		
		Action discard = action("Discard").poss(move("card", 1, 4));
		penguin_draw.trigger(discard);
		
		Action chain_search = action("Search off chain").hopt().poss(move("frightfur", 0, 1));
		MT.add(chain, list(1,2,6,7), 4, chain_search);
		
		Action special_sabres = action("Sabres special summon self from GY").open().hopt().poss(move(sabres, 4, 2), move("card", 1, 0));

		Action vendor_sabres_combo = action("Sabres vendor combo").hopt().open().off().poss(move(sabres, 4, 2), move("fluffal", 1, 2), move("card", 1, 4), cond(vendor,3));
		special_sabres.turnoff(vendor_sabres_combo);
		vendor_sabres_combo.turnoff(special_sabres);
		
		Action fusion_processing = new Action("Fusion processing").move_all(7, 4);

		Action poly_fusion = new Action("Polymerization Fusion Summon").open().trigger(fusion_processing);
		poly_fusion.poss(move(poly, 1, 4),move("edge", list(1,2), 7),move("fluffal", list(1,2), 7), move(whale, -1, 2));
		poly_fusion.poss(move(poly, 1, 4),move(sabres, list(1,2), 7),move("fluffal", list(1,2), 7), move(tiger, -1, 2));
		
		Action frightfur_fusion = action("Frightfur Fusion Effect").open().hopt();
		frightfur_fusion.poss(move(frightfur_fus, 1, 4),move("edge", list(2,4), 5),move("fluffal", list(2,4), 5), move(whale, -1, 2) );
		frightfur_fusion.poss(move(frightfur_fus, 1, 4),move(sabres, list(2,4), 5),move("fluffal", list(2,4), 5), move(tiger, -1, 2) );
		
		Action pop_vendor = action("Pop Vendor").poss(move(vendor, list(1,3), 4));
		MT.add(tiger, -1, 2, pop_vendor);
		
		Action send_repair = action("Whale send repair").hopt().poss(move(repair, 0, 4));
		MT.add(whale, -1, 2, send_repair);
		
		Action summon_shark = action("Make Bahamut Shark").hopt().open().poss(move(2, "lv4water", 2, 4), move(shark, -1, 2));
				
		Action summon_toad = action("Summon toad").poss(move(toad,-1,2));
		summon_shark.trigger(summon_toad);
		
		Action summon_cross = action("Summon cross-sheep").open().hopt().poss(move(2, "card", 2, 4), move(cross,-1,2));
		
		Action cross_special = action("Cross Sheep eff to special").hopt().poss(cond(cross, 2), move("ns", 4, 2)).poss(cond(cross, 2), move("dugcross_spec", 4, 2));
		MT.add("fusion", -1, 2, cross_special);
		
		Action dolphin_eff = action("Dolphin eff").open().hopt();
		dolphin_eff.poss(cond(dolphin, 2), move(wings, 0,4), move(vendor, 4, 3)).poss(cond(dolphin, 2), move(sabres, 0,4), move(vendor, 4, 3));
		
		Action octo_eff = action("Octo eff to add back").hopt().poss(move("fluffal", 4, 1)).poss(move("edge", 4, 1));
		MT.add(octo, list(0,1,4,5), 2, octo_eff);
		
		Action sheep_summon = action("Special sheep").open().poss(move(sheep,1,2), cond("fluffal",2).exclude(sheep));
		
		Action sheep_return = action("Sheep bounce eff").hopt().open().poss(move("edge", 4, 2), cond(sheep, 2), move("fluffal", 2, 1).exclude(sheep));
		
		Action wings_eff = action("Wings eff").open().hopt().draw(1,2).poss(cond(2, "fluffal", 4), move(wings, 4,5), move("fluffal",4,5), move(vendor,3,4));
		
		Action vendor_pitch = action("Vendor pitch").hopt().open().off().poss(cond(vendor,3), move("card",1,4));
		Action vendor_hit_processing = action("Vendor hit processing");
		Action vendor_special = action("Vendor hit! Special Summon");
	
		vendor_pitch.turnoff(vendor_sabres_combo);
		vendor_pitch.off();
		vendor_pitch.draw(6, 1);
		vendor_pitch.trigger(vendor_hit_processing);
		
		vendor_hit_processing.poss(vendor_special,move("fluffal", 6, 1));
		vendor_hit_processing.poss(move("card", 6, 4));	
		
		vendor_special.poss(move("monster", 1, 2));
		
		Action activate_vendor = action("Play vendor").open().poss(move(vendor, 1, 3));
		activate_vendor.turnon(vendor_pitch).turnon(vendor_sabres_combo);
		bear_eff.turnon(vendor_pitch).turnon(vendor_sabres_combo);
		dolphin_eff.turnon(vendor_pitch).turnon(vendor_sabres_combo);
		
		Action goods_eff = action("goods eff").open().hopt().poss(move(goods,1,4), move("goods_target",0,4));
		
		Action gardens_eff = action("gardens add").open().hopt().poss(move(gardens,1,3), move(penguin,0,1));
		
		Action repair_revive = action("Repair eff to revive").open().hopt();
		repair_revive.poss(move(repair, 1, 4), move("fusion",4,-1), move("fluffal",4,2)).poss(move(repair, 1, 4), move("fusion",4,-1), move("edge",4,2));
		
		Action repair_special = action("Repair special from hand").open().hopt();
		repair_special.poss(move(repair,4,5), move("fluffal",1,2)).poss(move(repair,4,5), move("edge",1,2));
		repair_special.turnoff(repair_revive);
		repair_revive.turnoff(repair_special);
		
		Action patchwork_eff = action("Patchwork search").hopt().open().poss(move(patchwork,1,4), move(poly,0,1), move("edge",0,1));
		
		Action garden_pitch = action("Garden discard and drop level").open().hopt();
		garden_pitch.poss(cond(gardens, 3), move(penguin, 1,-1), move(penguin3, -1, 1), move("good_discard",1,4));
		garden_pitch.poss(cond(gardens, 3), move(penguin, 2,-1), move(penguin3, -1, 2), move("good_discard",1,4));		
		Action penguin_swapback = action("Restore penguin to lv. 4").poss(move(penguin, -1, 4), move(penguin3,4,-1));
		MT.add(penguin3, list(1,2,6,7), 4, penguin_swapback);
		
		Action special_dugares = action("Make Dugares").open().hopt().poss(move(2, "lv4",2,4), move("lv4",2,4), move(dugares,-1,2));
		Action dugares_draw = action("Draw off Dugares").open().hopt().poss(cond(dugares,2)).draw(1, 2).trigger(discard);
		Action dugares_special = action("Special off Dugares").open().hopt();
		dugares_special.poss(cond(dugares,2), move("ns",4,2));
		dugares_special.poss(cond(dugares,2), move("dugcross_spec",4,2));
		dugares_special.turnoff(dugares_draw);
		dugares_draw.turnoff(dugares_special);
		
		//Action summon_dagda = action("Summon dagda + activate eff").open().hopt().poss(move(2, "card", 2, 4), move(dagda,-1,2), move(arti_scythe,0,3));
		
		Action special_tornado = action("Make Tornado").open().hopt().poss(move(2, "lv4",2,4), move(tornado,-1,2));
		MT.add(tornado, -1, 2, pop_vendor);
		
		Action eldlich_effect = action("Eff of Eldlich").open().hopt().poss(move("s/t", list(1,3), 4), move(eldlich, 4, 2));
		
		//Action summon_apo = action("Summon Apo").open().hopt().poss(move(4, "card", 2, 4).distinct(), move(apo4,-1,2)).poss(move(2, "card", 2, 4).distinct(), move("link 2", 2, 4), move(apo3, -1, 2));
		
		//action("Activate Desires").open().hopt().poss(move(desires, 1, 4)).draw(8, 10).draw(1,2);
		
		Gov.poss(cond(toad,2));
		//Gov.poss(cond(arti_scythe, list(1,3)), cond(edge_scythe,1), cond("fluffal", list(1,2)));
		//Gov.poss(cond(arti_scythe, list(1,3)), cond(tornado, 2));*/
		//Gov.poss(cond(arti_scythe, list(1,3)), cond(edge_scythe,1), cond("fluffal", list(1,2)), cond(toad, 2), cond("apo", 2));
		//Gov.poss(cond(arti_scythe, list(1,3)), cond(tornado, 2), cond(toad, 2), cond("apo", 2));
		//Gov.poss(cond(arti_scythe, list(1,3)), cond(edge_scythe,1), cond("fluffal", list(1,2)), cond("apo", 2));
		//Gov.poss(cond(arti_scythe, list(1,3)), cond(tornado, 2), cond("apo", 2));
		//Gov.poss(cond(apo4,2));

		
		go("Toad", 100);
	}
	//Ignore below this line
	private static void go(String filename, int num_trials)
	{
		try{
			File f = new File(filename+".txt");
			FileWriter fw = new FileWriter(f);
			System.out.println(Gov.probability(fw, num_trials));
			fw.close();
		}
		
		catch(Exception e)
		{
		e.printStackTrace();
		}
	}
	private static void go(String filename, int num_trials, DeckEdit[] changes)
	{
		try{
			DeckEdit[] changed = Gov.ratios(filename, num_trials, changes);
		}
		
		catch(Exception e)
		{
		e.printStackTrace();
		}
	}
	private static int[] list(int... arr)
	{
		return arr;
	}
	private static Action[] list(Action... arr)
	{
		return arr;
	}
	private static MoveCondition move(String category, int in, int out)
	{
		return new MoveCondition(category, in, out);
	}
	private static MoveCondition move(String category, int[] in, int out)
	{
		return new MoveCondition(category, in, out);
	}
	private static MoveCondition move(Card card, int in, int out)
	{
		return new MoveCondition(card, in, out);
	}
	private static MoveCondition move(Card card, int[] in, int out)
	{
		return new MoveCondition(card, in, out);
	}
	private static MoveCondition move(int num, String category, int in, int out)
	{
		return new MoveCondition(num, category, in, out);
	}
	private static MoveCondition move(int num, String category, int[] in, int out)
	{
		return new MoveCondition(num, category, in, out);
	}
	private static MoveCondition move(int num, Card card, int in, int out)
	{
		return new MoveCondition(num, card, in, out);
	}
	private static MoveCondition move(int num, Card card, int[] in, int out)
	{
		return new MoveCondition(num, card, in, out);
	}
	private static Condition cond(int num, char symbol, String category, int... locations)
	{
		return new Condition(num, symbol, category, locations);
	}
	private static Condition cond(int num, char symbol, Card card, int... locations)
	{
		return new Condition(num, symbol, card, locations);
	}
	private static Condition cond(int num, String category, int... locations)
	{
		return new Condition(num, category, locations);
	}
	private static Condition cond(String category, int... locations)
	{
		return new Condition(category, locations);
	}
	private static Condition cond(int num, Card card, int... locations)
	{
		return new Condition(num, card, locations);
	}
	private static Condition cond(Card card, int... locations)
	{
		return new Condition(card, locations);
	}
	private static ZeroCondition zcond(String category, int... locations)
	{
		return new ZeroCondition(category, locations);
	}
	private static ZeroCondition zcond(Card c, int... locations)
	{
		return new ZeroCondition(c, locations);
	}
	private static Action action(String name)
	{
		return new Action(name);
	}
	private static Card card(String name, int quantity)
	{
		return new Card(name, quantity);
	}
	private static Card card(String name, int quantity, String... cats)
	{
		return new Card(name, quantity, cats);
	}
	private static Card card(String name, int quantity, int level)
	{
		return new Card(name, quantity, level);
	}
	private static Card card(String name, int quantity, int level, String... cats)
	{
		return new Card(name, quantity, level, cats);
	}
	private static void hand(int hand_size)
	{
		Gov.hand_size=5;
	}
	private static void locations(String... locations)
	{
		Gov.locations=locations;
	}
	public static Condition cond(int num, Card card)
	{
		return new Condition(num, card);
	}
	public static Condition cond(Card card)
	{
		return new Condition(card);
	}
	public static Condition cond(int num, String category)
	{
		return new Condition(num, category);
	}
	public static Condition cond(String category)
	{
		return new Condition(category);
	}
	public static void complete(int num)
	{
		if(Card.deck_size>num)
		{
			System.out.println("Deck inputs larger than deck size");
			System.exit(0);
		}
		card("blank", num-Card.deck_size);
	}
	public static void poss(Condition... conditions) //if something like allure, check for a dark to discard first 
	{
		Gov.poss(conditions);
	}
	public static void terminate(Action[] are_off, Condition... conds)
	{
		Gov.terminate(are_off, conds);
	}

}

