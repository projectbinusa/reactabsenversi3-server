import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarAdmin";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import { useParams, useHistory } from "react-router-dom";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../../utils/api";

function EditShift() {
  const [namaShift, setNamaShift] = useState("");
  const [waktuMasuk, setWaktuMasuk] = useState("");
  const [waktuPulang, setWaktuPulang] = useState("");
  const { id } = useParams();
  const history = useHistory();
  const idAdmin = localStorage.getItem("adminId");

  const getShift = async () => {
    try {
      const res = await axios.get(
        `${API_DUMMY}/api/shift/getbyId/${id}`
      );
      setNamaShift(res.data.namaShift);
      setWaktuMasuk(res.data.waktuMasuk);
      setWaktuPulang(res.data.waktuPulang);
    } catch (error) {
      console.log(error);
    }
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    const shift = {
      namaShift: namaShift,
      waktuMasuk: waktuMasuk,
      waktuPulang: waktuPulang,
    };
    try {
      await axios.put(
        `${API_DUMMY}/api/shift/editbyId/${id}?idAdmin=${idAdmin}`,
        shift,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      Swal.fire({
        title: "Berhasil",
        text: "Berhasil mengubah data shift",
        icon: "success",
        showConfirmButton: false,
      });

      setTimeout(() => {
        window.location.href = "/admin/shift";
      }, 2000);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getShift();
  }, [id]);

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container p-8 ml-14 md:ml-64 mt-12">
          <div className="p-4">
            <div className="p-5">
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Edit Shift
                  </h6>
                </div>

                <hr />

                <div className="mt-5 text-left">
                  <form onSubmit={handleSubmit}>
                    <input type="hidden" name="id_shift" value={id} />

                    <div className="grid md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="nama_shift"
                          id="nama_shift"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder="Nama Shift"
                          onChange={(e) => setNamaShift(e.target.value)}
                          autoComplete="off"
                          required
                          value={namaShift}
                        />
                        <label
                          htmlFor="nama_shift"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Nama
                        </label>
                      </div>

                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="time"
                          name="jam_masuk"
                          id="jam_masuk"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          onChange={(e) => setWaktuMasuk(e.target.value)}
                          value={waktuMasuk}
                        />
                        <label
                          htmlFor="jam_masuk"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Jam Masuk
                        </label>
                      </div>
                    </div>

                    <div className="grid md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="time"
                          name="jam_pulang"
                          id="jam_pulang"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          onChange={(e) => setWaktuPulang(e.target.value)}
                          value={waktuPulang}
                        />
                        <label
                          htmlFor="jam_pulang"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Jam Pulang
                        </label>
                      </div>
                    </div>

                    <div className="flex justify-between">
                      <a
                        className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                        href="/admin/shift"
                      >
                        <FontAwesomeIcon icon={faArrowLeft} />
                      </a>
                      <button
                        type="submit"
                        className="text-white bg-indigo-500 hover:bg-indigo-800 focus:ring-4 focus:ring-indigo-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-indigo-600 dark:hover:bg-indigo-700 focus:outline-none dark:focus:ring-indigo-800"
                      >
                        <FontAwesomeIcon icon={faFloppyDisk} />
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditShift;
