import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class Project1 {
	public static void main(String[] args){
		File file = new File(args[0], "branch.lst");
		if(!file.exists()){
			System.out.println(file + "支店定義ファイルが存在しません");
			return;
		}


		HashMap<String, String> branchmap = new HashMap<String,String>();

		try{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null){
				String[] st = s.split(",");


				// st[0] = 支店コードに入っている値が数字三桁か判定
				if(!st[0].matches("\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
				}
				// st[1] = 支店名に入っている文字列にカンマ・改行が入っていないか判定
				if(st[1].indexOf(",") == 1) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
				}
				branchmap.put(st[0],st[1]);

			}
			br.close();
		}catch(IOException e){
			System.out.println(e);
		}



		File file2 = new File(args[0], "commodity.list");
		if(!file2.exists()){
			System.out.println(file2 + "支店定義ファイルが存在しません");
			return;
		}

		HashMap<String, String> commoditymap = new HashMap<String,String>();
		try{
			FileReader fr2 = new FileReader(file2);
			BufferedReader br2 = new BufferedReader(fr2);
			String s2;
			while((s2 = br2.readLine()) != null){
				String[] st2 = s2.split(",");


				// st2[0] = 商品コードに入っている値がアルファベットと数字八桁か判定
				if(!st2[0].matches("\\w{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
				}
				// st2[1] = 商品名に入っている文字列にカンマ・改行が入っていないか判定
				if(st2[1].indexOf(",") == 1) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
				}
				commoditymap.put(st2[0],st2[1]);
			}
			br2.close();
		}catch(IOException e){
			System.out.println(e);
		}


		File files = new File(args[0], "\\d{8}.rcd");



	}
}
