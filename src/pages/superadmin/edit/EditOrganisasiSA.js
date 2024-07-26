import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useHistory, useParams } from "react-router-dom";
import axios from "axios";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../../utils/api";

function EditOrganisasiSA() {
  const [email, setEmail] = useState("");
  const [noTelepon, setNoTelepon] = useState("");
  const [kecamatan, setKecamatan] = useState("");
  const [alamat, setAlamat] = useState("");
  const [kabupaten, setKabupaten] = useState("");
  const [provinsi, setProvinsi] = useState("");
  const [namaOrganisasi, setNamaOrganisasi] = useState("");
  const [image, setImage] = useState(null);
  const param = useParams();
  const history = useHistory();
  const [idAdmin, setIdAdmin] = useState("");

  useEffect(() => {
    axios
      .get(`${API_DUMMY}/api/organisasi/getById/` + param.id, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      })
      .then((ress) => {
        const response = ress.data;
        setNamaOrganisasi(response.namaOrganisasi);
        setAlamat(response.alamat);
        setKecamatan(response.kecamatan);
        setKabupaten(response.kabupaten);
        setProvinsi(response.provinsi);
        setNoTelepon(response.nomerTelepon);
        setEmail(response.emailOrganisasi);
        setImage(response.fotoOrganisasi);
        setIdAdmin(response.admin.id);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const updateOrganisasi = async (e) => {
    e.preventDefault();

    const org = {
      namaOrganisasi: namaOrganisasi,
      alamat: alamat,
      kecamatan: kecamatan,
      kabupaten: kabupaten,
      provinsi: provinsi,
      emailOrganisasi: email,
      nomerTelepon: noTelepon,
    };

    const formData = new FormData();
    formData.append(
      "organisasi",
      new Blob([JSON.stringify(org)], { type: "application/json" })
    );
    if (image) {
      formData.append("image", image);
    }

    await axios
      .put(
        `${API_DUMMY}/api/organisasi/editById/${param.id}?idAdmin=${idAdmin}`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      )
      .then(() => {
        Swal.fire({
          icon: "success",
          title: "Berhasil Mengubah Data",
          showConfirmButton: false,
          timer: 1500,
        });
        history.push("/superadmin/organisasi");
      })
      .catch((error) => {
        console.log(error);
        Swal.fire("Gagal", "Gagal memperbarui data", "error");
      });
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
      </div>
      <div className="sm:ml-64 content-page p-8 ml-14 md:ml-64 mt-14">
        <div className="p-4">
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Edit Organisasi
              </h6>
            </div>

            <hr />

            <div className="mt-5 text-left">
              <form onSubmit={updateOrganisasi} encType="multipart/form-data">
                <input type="hidden" name="id_organisasi" value="8" />
                <div className="grid md:grid-cols-2 md:gap-6">
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="nama_organisasi"
                      id="nama"
                      value={namaOrganisasi}
                      onChange={(e) => setNamaOrganisasi(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="nama"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Nama
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="email"
                      name="email_organisasi"
                      id="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="email"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Email
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="nomer_telepon"
                      id="noTelepon"
                      value={noTelepon}
                      onChange={(e) => setNoTelepon(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="noTelepon"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Nomer Telepon
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="alamat"
                      id="alamat"
                      value={alamat}
                      onChange={(e) => setAlamat(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="alamat"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Alamat
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="kecamatan"
                      id="kecamatan"
                      value={kecamatan}
                      onChange={(e) => setKecamatan(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="kecamatan"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Kecamatan
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="kabupaten"
                      id="kabupaten"
                      value={kabupaten}
                      onChange={(e) => setKabupaten(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="kabupaten"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Kabupaten
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="text"
                      name="provinsi"
                      id="provinsi"
                      value={provinsi}
                      onChange={(e) => setProvinsi(e.target.value)}
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      placeholder=" "
                      autoComplete="off"
                      required
                    />
                    <label
                      htmlFor="provinsi"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Provinsi
                    </label>
                  </div>
                  <div className="relative z-0 w-full mb-6 group">
                    <input
                      type="file"
                      name="image"
                      id="image"
                      onChange={(e) => setImage(e.target.files[0])} // Change this line to use e.target.files[0]
                      className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                      autoComplete="off"
                    />
                    <label
                      htmlFor="image"
                      className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                    >
                      Foto Organisasi
                    </label>
                  </div>
                </div>

                <div className="flex justify-between">
                  <a
                    className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                    href="/superadmin/organisasi"
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
  );
}

export default EditOrganisasiSA;
