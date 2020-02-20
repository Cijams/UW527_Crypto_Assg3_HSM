import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../api.service';

const localUrl = 'assets/data/smartphone.json';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.css']
})
export class CryptoComponent implements OnInit {
  notebooks: Notebook[] = [];

  constructor(private http: HttpClient, private apiService: ApiService) { }

  ngOnInit(): void {
    this.test1();
    this.test2();
  }

  onGenerateKeys() {
  }

  public test1() {
    const url = 'http://localhost:8080/hash';
    this.http.get(url,
      {responseType: 'text'}).subscribe(
      res => {
        console.log(res);
      },
     // err => {
     //   alert("Error");
     //   console.log(err)
  //    }
    );
  }

public test2() {
  const url = 'http://localhost:8080/encrypt';
  this.http.get(url,
    {responseType: 'json'}).subscribe(
    res => {
      console.log(res);
    },
   // err => {
   //   alert("Error");
   //   console.log(err)
//    }
  );
}
}



export interface Notebook {
  id: string;
  name: string;
  nbOfBotes: number;
}

export interface Note {
  id:string;
  title:string;
  text:string;
  notebookId:string;
  lastModifiedOn: string;
}