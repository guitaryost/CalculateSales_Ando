

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;


public class CalculateSales {
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		File branchFile = new File(args[0], "branch.lst");
		if(!branchFile.exists()){
			System.out.println(branchFile + "支店定義ファイルが存在しません");
			return;
		}
		HashMap<String, String> branchMap = new HashMap<String, String>();
		HashMap<String, Long> branchEarningsMap = new HashMap<String, Long>();
		BufferedReader branchbr = null;
		try{
			branchbr = new BufferedReader(new FileReader(branchFile));
			String branchSt;
			while((branchSt = branchbr.readLine()) != null){
				String[] branchSts = branchSt.split(",");
				// branchSts[0] = 支店コードに入っている値が数字三桁か判定
				if(!branchSts[0].matches("\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				// branchSts[1] = 支店名に入っている文字列にカンマ・改行が入っていないか判定
				if(branchSts[1].indexOf("\n") == 1) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branchMap.put(branchSts[0], branchSts[1]);
				branchEarningsMap.put(branchSts[0], 0l);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}finally{
			try {
				if(branchbr != null){
					branchbr.close();
				}
			}catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		File commodityFile = new File(args[0], "commodity.lst");
		if(!commodityFile.exists()){
			System.out.println(commodityFile + "商品定義ファイルが存在しません");
			return;
		}
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long> commodityEarningsMap = new HashMap<String, Long>();
		BufferedReader commoditybr = null;
		try{
			commoditybr = new BufferedReader(new FileReader(commodityFile));
			String commoditySt;
			while((commoditySt = commoditybr.readLine()) != null){
				String[] commoditySts = commoditySt.split(",");
				// commoditySts[0] = 商品コードに入っている値がアルファベットと数字八桁か判定
				if(!commoditySts[0].matches("\\w{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				// commoditySts[1] = 商品名に入っている文字列にカンマ・改行が入っていないか判定
				if(commoditySts[1].indexOf("\n") == 1) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityMap.put(commoditySts[0], commoditySts[1]);
				commodityEarningsMap.put(commoditySts[0], 0l);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}finally{
			try{
				if(commoditybr != null){
					commoditybr.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		//売上ファイル名の連番チェック
		File allFiles = new File(args[0]);
		String[] allFileList = allFiles.list();
		ArrayList<String> earnings = new ArrayList<String>();
		for(String allFileListSt: allFileList){
			//rcdリスト作成、add
			if(allFileListSt.matches("\\d{8}.rcd$")){
				earnings.add(allFileListSt);
			}
		}
		Collections.sort(earnings);
		//rcdリスト抽出、ループ
		for(int i = 0;i < earnings.size(); i++){
			if(Integer.parseInt(earnings.get(i).split("\\.")[0]) - i != 1){
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}
		//.rcdのファイルから値を抽出し、それぞれを加算する。
		BufferedReader rcdbr = null;
		for(int i = 0; i < earnings.size(); i++){
			File rcdFiles = new File(args[0], earnings.get(i));
			ArrayList<String> rcdData = new ArrayList<String>();
			try{
				FileReader rcdfr = new FileReader(rcdFiles);
				rcdbr = new BufferedReader(rcdfr);
				String rcdSt;
				while((rcdSt = rcdbr.readLine()) != null){
					rcdData.add(rcdSt);
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}finally{
					try{
						if(rcdbr != null);
						rcdbr.close();
					}catch(IOException e){
						System.out.println("予期せぬエラーが発生しました");
						return;
			}
			//rcdDataの要素数を比較
			if(rcdData.size() >= 4){
				System.out.println( earnings + "のフォーマットが不正です");
				return;
			}
			if(branchEarningsMap.get(rcdData.get(0)) == null){
				System.out.println( earnings + "の支店コードが不正です");
				return;
			}
			//rcdDataが10桁を越えているか確認
			if(Long.parseLong(rcdData.get(2)) > 9999999999.9){
				System.out.println("合計金額が10桁を超えました");
				return;
			}

			// 1.新しい金額を取得
			long rcdEarningA = Long.parseLong(rcdData.get(2));
			// 2.既存の値を取得。Mapに格納されている値を呼び出して取得する。
			long rcdEarningB = branchEarningsMap.get(rcdData.get(0));
			// 3.新しい値と既存の値を加算する
			long branchSales = rcdEarningA + rcdEarningB;
			//合計金額の桁数チェック
			if(branchSales > 9999999999.9){
				System.out.println("合計金額が10桁を超えました");
				return;
			}
			// 4. 3で加算した値をしまう
			branchEarningsMap.put(rcdData.get(0), branchSales);
			if(commodityEarningsMap.get(rcdData.get(1)) == null){
				System.out.println( earnings + "の商品コードが不正です");
				return;
			}
			long rcdEarningC = Integer.parseInt(rcdData.get(2));
			long rcdEarningD = commodityEarningsMap.get(rcdData.get(1));
			long commodityEarningsSales = rcdEarningC + rcdEarningD;
			if(commodityEarningsSales  > 9999999999.9){
				System.out.println("合計金額が10桁を超えました");
				return;
			}
			commodityEarningsMap.put(rcdData.get(1),commodityEarningsSales);
		}

		//集計結果出力
		//List 生成 (ソート用)
		ArrayList<Entry<String, Long>> branchAggregateList =new ArrayList<Entry<String,Long>>(branchEarningsMap.entrySet());
		Collections.sort(branchAggregateList, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(
					Entry<String, Long> branchAggregate1, Entry<String, Long> branchAggregate2) {
				return ((Long)branchAggregate2.getValue()).compareTo((Long)branchAggregate1.getValue());
			}
		});
		//内容を表示
		BufferedWriter branchAggeregateBw = null;
		try{
			File branchAggregateFile = new File(args[0], "branch.out");
			FileWriter branchAggregateFw = new FileWriter(branchAggregateFile);
			branchAggeregateBw = new BufferedWriter(branchAggregateFw);
			for (Entry<String,Long> branchAggregateSt : branchAggregateList) {
				branchAggeregateBw.write(branchAggregateSt.getKey() + "," +
						branchMap.get(branchAggregateSt.getKey()) + "," +
						branchAggregateSt.getValue() + "\n");
			}
			branchAggeregateBw.close();
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}finally{
			try{
				if(branchAggeregateBw != null){
					branchAggeregateBw.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}

		ArrayList<Entry<String, Long>> commodityAggregateList =new ArrayList<Entry<String,Long>>(commodityEarningsMap.entrySet());
		Collections.sort(commodityAggregateList, new Comparator<Entry<String, Long>>() {
			 @Override
			 public int compare(
					Entry<String, Long> commodityAggregate1, Entry<String, Long> commodityAggregate2) {
				 	return ((Long)commodityAggregate2.getValue()).compareTo((Long)commodityAggregate1.getValue());
			 }
		 });
		 //内容を表示
		BufferedWriter commodityAggeregateBw = null;
		 try{
			 File commodityAggregateFile = new File(args[0], "commodity.out");
			 FileWriter commodityAggregateFw = new FileWriter(commodityAggregateFile);
			 commodityAggeregateBw = new BufferedWriter(commodityAggregateFw);
			 for (Entry<String,Long> commodityAggregateSt : commodityAggregateList) {
				 commodityAggeregateBw.write(commodityAggregateSt.getKey() + "," +
						 commodityMap.get(commodityAggregateSt.getKey()) + "," +
						 commodityAggregateSt.getValue() + "\n");
			 }
		 }catch(IOException e){
			 System.out.println("予期せぬエラーが発生しました");
			 return;
		 }finally{
			 try{
				 if(commodityAggeregateBw != null){
					 commodityAggeregateBw.close();
				 }
			 }catch(IOException e){
				 System.out.println("予期せぬエラーが発生しました");
				 return;
			 }
		}
	}
}
	}
