package jp.co.plusize.ando_ryosuke.calculate_sales;

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
import java.util.Map;
import java.util.Map.Entry;



public class CalculateSales {
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		HashMap<String, String> branchMap = new HashMap<String, String>();
		HashMap<String, Long> branchEarningsMap = new HashMap<String, Long>();

		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long> commodityEarningsMap = new HashMap<String, Long>();

		//ブランチの入力
		if(!input(branchMap, branchEarningsMap, args[0], "branch.lst", "支店", "\\d{3}$")){
			return;
		}
		//商品の入力
		if(!input(commodityMap, commodityEarningsMap, args[0], "commodity.lst", "商品", "\\w{8}$")){
			return;
		}

		//売上ファイル名の連番チェック
		File allFiles = new File(args[0]);
		String[] allFileList = allFiles.list();

		ArrayList<String> earnings = new ArrayList<String>();
		for(String allFileListSt: allFileList){
			File file = new File(args[0], allFileListSt);
			//rcdリスト作成、add
			if(allFileListSt.matches("\\d{8}.rcd$") && file.isFile()){
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
					if(rcdbr != null){
						rcdbr.close();
					}
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
			//rcdDataの要素数を比較
			if(rcdData.size() != 3){
				System.out.println( earnings.get(i) + "のフォーマットが不正です");
				return;
			}
			if(!branchEarningsMap.containsKey(rcdData.get(0))){
				System.out.println(earnings.get(i) + "の支店コードが不正です");
				return;
			}
			try{
				// 1.新しい金額を取得
				long rcdEarningA = Long.parseLong(rcdData.get(2));


				if( rcdEarningA > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				// 2.既存の値を取得。Mapに格納されている値を呼び出して取得する。
				long rcdEarningB = branchEarningsMap.get(rcdData.get(0));

				// 3.新しい値と既存の値を加算する
				long branchSales = rcdEarningA + rcdEarningB;

				//合計金額の桁数チェック
				if(branchSales > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				// 4. 3で加算した値をしまう
				branchEarningsMap.put(rcdData.get(0), branchSales);

				if(!commodityEarningsMap.containsKey(rcdData.get(1))){
					System.out.println( earnings.get(i) + "の商品コードが不正です");
					return;
				}
				long rcdEarningD = commodityEarningsMap.get(rcdData.get(1));
				long commodityEarningsSales = rcdEarningA + rcdEarningD;
				if(commodityEarningsSales  > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				commodityEarningsMap.put(rcdData.get(1),commodityEarningsSales);
			}catch(NumberFormatException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}

		// ブランチの出力
		if(!output(branchEarningsMap, branchMap, args[0], "branch.out")){
			return;
		}
		// 商品の出力
		if(!output(commodityEarningsMap, commodityMap, args[0], "commodity.out")){
			return;
		}
	}

	//メソッドを新たに作成(インプットメソッド)
	public static boolean input(Map<String, String> nameMap, Map<String, Long> earningsMap, String path, String fileName, String name, String math){
		File file = new File(path, fileName);
		if(!file.exists()){
			System.out.println( name + "定義ファイルが存在しません");
			return false;
		}
		BufferedReader br = null;
		try{
			String st;
			br = new BufferedReader(new FileReader(file));
			while((st = br.readLine()) != null){
				String[] stList = st.split(",");
				// commoditySts[0] = 商品コードに入っている値がアルファベットと数字八桁か判定
				if(!stList[0].matches(math)) {
					System.out.println(name + "定義ファイルのフォーマットが不正です");
					return false;
				}
				// commodityStsの配列の数を判定(2つあるかどうか)
				if(stList.length != 2) {
					System.out.println(name + "定義ファイルのフォーマットが不正です");
					return false;
				}
				nameMap.put(stList[0], stList[1]);
				earningsMap.put(stList[0], 0l);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}

	//メソッドを新たに作成(アウトプットメソッド)
	public static boolean output(Map<String, Long> earningsMap, Map<String, String> nameMap, String path, String fileName){
		ArrayList<Entry<String, Long>> sortList =new ArrayList<Entry<String,Long>>(earningsMap.entrySet());
		Collections.sort(sortList, new Comparator<Entry<String, Long>>() {
			 @Override
			 public int compare(
				Entry<String, Long> e1, Entry<String, Long> e2) {
				return ((Long)e2.getValue()).compareTo((Long)e1.getValue());
			 	}
		});
		 //内容を表示
		BufferedWriter bw = null;
		 try{
			 File file = new File(path, fileName);
			 FileWriter fw = new FileWriter(file);
			 bw = new BufferedWriter(fw);
			 for (Entry<String,Long> st : sortList) {
				 bw.write(st.getKey() + "," +
						 nameMap.get(st.getKey()) + "," +
						 st.getValue() + "\n");
			 }
		 }catch(IOException e){
			 System.out.println("予期せぬエラーが発生しました");
			 return false;
		 }finally{
			  try{
				 if(bw != null){
					 bw.close();
				 }
			  }catch(IOException e){
				 System.out.println("予期せぬエラーが発生しました");
				 return false;
			  }
		 }
		 return true;
	}
}
