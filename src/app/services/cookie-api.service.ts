import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class CookieApiService {

  public constructor(private http: HttpClient) {}

  public getCookie(): Observable<void> {
    return <Observable<void>> this.http.post('cookie', null).pipe(map(() => {}));
  }

}
