import { Component, OnInit } from '@angular/core';
import _ from 'lodash';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'Frontend';

  ngOnInit(): void {

    if (typeof $ === "undefined") {
      console.error("jQuery is not loaded! Please wait!!");
      return;
    }

    $("#btn").on("click", () => {
      $(".aa").css("color", "red");
      alert("This is a horrible Test!!");
    });

    _.isEmpty(null);
  }
}
